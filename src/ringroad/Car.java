package ringroad;

import java.util.Random;

public class Car {

	public static Field field;

	private int[] origin;
	private int[] destination;
	//
	private final int X = 0;
	private final int Y = 1;
	private final int ISEC = 2;
	private final int STEP = 3;

	// この車の現在地情報
	public int curPosX; // fieldのx座標(円筒系)
	public int curPosY; // fieldのy座標(円筒系)
	public int curIsec; // 交差点番号
	public int curStep; // 道路サイト中での位置

	// 経路情報
	private int[][] route;
	private int routeStep;

	/**
	 * コンストラクタ。車を生成する
	 *
	 * @param x    出発地のx座標
	 * @param y    出発地のy座標
	 * @param isec 出発地の交差点番号
	 * @param step 出発地の道路サイトのステップ数
	 */
	public Car(int x, int y, int isec, int step) {
		origin = new int[] {x, y, isec, step};

		curPosX = x;
		curPosY = y;
		curIsec = isec;
		curStep = step;

		// 目的地を決定する
		setDestination();
		// 経路を決定する
		setRoute();
		routeStep = 0;

	}

	// 目的地を決定する
	private void setDestination() {
		Random random = new Random();

		int nx = field.intersections.length;
		int ny = field.intersections[0].length;

		boolean flag = false;
		int rx, ry, ri, rs;
		do {
			rx = random.nextInt(nx);
			ry = random.nextInt(ny);
			ri = random.nextInt(4);
			int ni = field.intersections[nx][ny].lengthAt(ri);
			if (ni == 0) continue;
			rs = random.nextInt(ni);
			// 出発地と完全に一致した場合は抽選し直し
			if (origin[X] == rx &&
				origin[Y] == ry &&
				origin[ISEC] == ri &&
				origin[STEP] == rs) continue;
			// 目的地を保存する
			destination = new int[] {rx, ry, ri, rs};
			flag = true;
		} while (flag);
	}


	// 経路を決定する
	private void setRoute() {
		int numX = field.intersections.length;
		int numY = field.intersections[0].length;

		// とりあえずroute変数は最大数+少し余裕を持って確保しておく。
		route = new int[numX/2 + numY*2 + 2][3];

		// 特例: 同じ道路サイト内の場合(交差点サイトを全く通らない場合)
		if (origin[X] == destination[X] &&
			origin[Y] == destination[Y] &&
			origin[ISEC] == destination[ISEC] &&
			origin[STEP] > 0 && origin[STEP] < destination[STEP]) {
			// routeにダミー値をセットして終了
			route[0][0] = -1;
			route[0][1] = -1;
			route[0][2] = -1;
			return;
		}

		// 出発して1番目に通る交差点の座標
		int origX, origY;
		if (origin[STEP] == 0) {
			origX = origin[X];
			origY = origin[Y];
		} else {
			Intersection temp = field.intersections[origin[X]][origin[Y]].neighbors()[origin[ISEC]];
			origX = temp.x();
			origY = temp.y();
		}

		// 目的地に着く前に通る最後の交差点の座標
		int destX, destY;
		destX = destination[X]; // 交差点サイト・道路サイト問わずこれで良い
		destY = destination[Y];

		// origXとdestXが、中心からの角度が 2rad 以上ある場合、内側を通ったほうが近くなる。
		// 2rad 未満の場合、外側を通ったほうが近くなる。

		int moveX;

		int xdir; // 1=正回り, 0=X方向移動なし, -1=負回り  (正回りとは、xのインデックス増大方向に動くこと)
		if (origX == destX) {
			// X方向移動なし
			xdir = 0;
			moveX = 0; // X方向の移動数
		} else {
			// X方向: 正回りor負回りを決定
			if (destX > origX) {
				int dox = destX - origX;
				if (dox <= numX - dox) {
					xdir = 1; // 正回り
					moveX = dox;
				} else {
					xdir = -1; // 負回り
					moveX = numX - dox;
				}
			} else {
				// @when (destX < origX)
				int odx = origX - destX;
				if (odx <= numX - odx) {
					xdir = -1; // 負回り
					moveX = odx;
				} else {
					xdir = 1; // 正回り
					moveX = numX - odx;
				}
			}
		}

		int moveY;
		int ydir; // 1=下り, 0=Y方向移動なし, -1=上り  (下りとは、yのインデックス増大方向(外方向)に動くこと)
		if (origY == destY) {
			// Y方向移動なし
			ydir = 0;
			moveY = 0;
		} else {
			// Y方向: 下りor上りを決定
			if (origY < destY) {
				ydir = 1;
				moveY = destY - origY;
			}
			else {
				ydir = -1;
				moveY = origY - destY;
			}
		}

		int dir; // 外回りor内回りを決定  (1=外回り=外側指向, 0=内回り=内側指向)
		if (2*Math.PI*moveX/numX <= 2/*[rad]*/) {
			dir = 1; // 2rad未満のとき、外回り
		} else {
			dir = 0; // 2rad以上のとき、内回り
		}

		// これ以降、実際に経路をセットしていく
		if (xdir == 0 && ydir == 0) {
			// 出発地が自分の交差点サイト内か、接続してくる道路サイト内にいて、
			// 目的地も自分の交差点内にある場合
			route[0][0] = origX;
			route[0][1] = origY;
			route[0][2] = destination[ISEC];

		} else if (xdir == 0) {
			// y方向にのみ進む
			int i;
			for (i = 0; i < moveY; i++) {
				route[i][0] = origX;
				route[i][1] = origY + (ydir * i);
				route[i][2] = (ydir == 1 ? 3 : 1);
			}
			route[i-1][2] = destination[ISEC];

		} else {
			// x方向の動きがある場合
			if (dir == 1) {
				// 外回り
				if (ydir == 0) {
					// x方向へ移動するだけ
				} else if (ydir == -1) {
					// y方向へ動いてからx方向へ動く
				} else /* @when (ydir == 1) */ {
					// x方向へ動いてからy方向へ動く

				}

/*
				int i;

				for (i = 0; i < moveY; i++) {
					route[i][0] = origX;
					route[i][1] = origY + (i * ydir);
					route[i][2] = (ydir > 0 ? 3 : 1);
				}
				if (i == 0) {
					route[0][0] = origX;
					route[0][1] = origY;
					route[0][2] = ?;
				} else {
					i--;
				}
*/

			} else {
				// 内回り



			}

			/*
			// x方向にのみ進む
			int i;
			for (i = 0; i < moveX; i++) {
				route[i][0] = (origX + (xdir * i)) % numX;
				route[i][1] = origY;
				route[i][2] = (xdir == 1 ? 2 : 0);
			}
			route[i-1][2] = destination[ISEC];
*/

		}

	}





	// 次の交差点で抜ける交差点番号を返す
	public int outIsec() {
		return 0;//XXX
	}
}