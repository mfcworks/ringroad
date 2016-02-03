package ringroad;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Car {

	public static Field field;

	public static List<Car> carList = new LinkedList<Car>();

	// int[4] の位置情報の配列のインデックスには次の定数を用いる
	private static final int X = 0;
	private static final int Y = 1;
	private static final int ISEC = 2;
	private static final int STEP = 3;


	/**
	 * 出発地の位置(x, y, isec, step)
	 */
	private int[] origin;
	/**
	 * 目的地の位置(x, y, isec, step)
	 */
	private int[] destination;
	/**
	 * 現在位置(x, y, isec, step)
	 */
	private int[] current;


	/**
	 * 経路情報
	 * route[i][X]は i番目に通るの交差点のX座標、
	 * route[i][Y]は i番目に通るの交差点のY座標、
	 * route[i][ISEC]は i番目に通る交差点を抜ける交差点番号を格納する。
	 */
	public int[][] route;
	private int routeStep;

	/**
	 * コンストラクタ。
	 * 出発地を指定して車を作成します。
	 * 目的地はランダムに決定されます。
	 *
	 * @param x    出発地のx座標
	 * @param y    出発地のy座標
	 * @param isec 出発地の交差点番号
	 * @param step 出発地の道路サイトのステップ数
	 */
	public Car(int x, int y, int isec, int step) {
		// 出発地を格納する
		origin = new int[] {x, y, isec, step};
		// 出発時は出発地にいる
		current = new int[] {x, y, isec, step};

		System.out.println("Car is being created:");
		System.out.println("  origin: {" + origin[0] + "," + origin[1] + "," + origin[2] + "," + origin[3] + "}");

		// 目的地を決定する
		setDestination();
		// 経路を決定する
		setRoute();
		routeStep = 0;

		// リストに追加
		carList.add(this);

		// DEBUG
		System.out.println("  destination: {" + destination[0] + "," + destination[1] + "," + destination[2] + "," + destination[3] + "}");
		routeInfo();
	}

	/**
	 * 目的地をランダムに決定します。
	 */
	private void setDestination() {
		Random random = new Random();

		boolean flag = true;
		int rx, ry, ri, rs;
		do {
			rx = random.nextInt(field.numX);
			ry = random.nextInt(field.numY);
			ri = random.nextInt(4);
			int ni = field.lengthAt(rx, ry, ri) + 1;
			rs = random.nextInt(ni);

			// 出発地と完全に一致した場合は抽選し直し
			if (origin[X] == rx &&
				origin[Y] == ry &&
				origin[ISEC] == ri &&
				origin[STEP] == rs) continue;
			// 目的地を保存する
			destination = new int[] {rx, ry, ri, rs};
			flag = false;
		} while (flag);
	}

	/**
	 * 出発地から目的地までの経路を決定し、経路情報を
	 * 配列変数 route に格納します。
	 */
	private void setRoute() {
		int numX = field.numX;
		int numY = field.numY;

		// とりあえずroute変数は最大数+少し余裕を持って確保しておく。
		route = new int[numX/2 + numY*2 + 2][3];

		// 特例: 同じ道路サイト内の場合(交差点サイトを全く通らない場合)
		if (origin[X] == destination[X] &&
			origin[Y] == destination[Y] &&
			origin[ISEC] == destination[ISEC] &&
			origin[STEP] > 0 && origin[STEP] < destination[STEP]) {
			// routeにダミー値をセットして終了
			route[0][X] = -1;
			route[0][Y] = -1;
			route[0][ISEC] = -1;
			return;
		}

		// 出発して1番目に通る交差点の座標
		int origX, origY;
		if (origin[STEP] == 0) {
			origX = origin[X];
			origY = origin[Y];
		} else {
			Intersection temp = field.getIntersection(origin[X], origin[Y]).neighbor(origin[ISEC]);
			origX = temp.thisX;
			origY = temp.thisY;
		}

		// 目的地に着く前に通る最後の交差点の座標
		int destX, destY;
		destX = destination[X]; // 交差点サイト・道路サイト問わずこれで良い
		destY = destination[Y];

		// origXとdestXが、中心からの角度が 2rad 以上ある場合、内側を通ったほうが近くなる。
		// 2rad 未満の場合、外側を通ったほうが近くなる。

		/*
		 * ルーティング実装
		 * 交差点を経由するすべての車は、次の3つの順序でフィールドを通過する
		 * 1. 放射道路を上る(中心側へ行く) : inbound≧0
		 * 		外回りで放射道路を通行しない車は放射道路を移動しない
		 * 		外回りで目的地が出発地より内側にある場合、放射道路を動く
		 * 		内回りの場合、最内側の交差点まで動く
		 * 2. 環状道路を回る : ±ring
		 * 		環状道路を回らない場合、環状道路を移動しない
		 * 		正回りの場合ring>0、負回りの場合ring<0
		 * 3. 放射道路を下る(外側へ行く) : outbound≧0
		 * 		外回りで目的地が出発地より外側にある場合、放射道路を動く
		 * 		内回りの場合、目的地の交差点まで動く
		 */

		int inbound;	// 上り方向の移動数
		int ring;		// X方向の移動数(符号付き)
		int outbound;	// 下り方向の移動数

		// X方向：正回りor負回りを決める
		if (origX == destX) {
			// X方向移動なし
			ring = 0;
		} else if (origX < destX) {
			int t = destX - origX;
			ring = (t <= numX/2 ? t : numX-t);
		} else /* @when (origX > destX) */ {
			int t = origX - destX;
			ring = (t <= numX/2 ? -t : numX-t);
		}

		// 外回りor内回りを決める  (1=外回り=外側指向, 0=内回り=内側指向)
		int dir;
		if (2*Math.PI*Math.abs(ring)/numX <= 2/*[rad]*/) {
			dir = 1; // 2rad未満のとき、外回り
		} else {
			dir = 0; // 2rad以上のとき、内回り
		}

		// inboundを求める
		if (dir == 0) {
			// 内回りの場合
			inbound = origY;
		} else if (origY > destY) {
			// 外回りで目的地のほうが内側にある場合
			inbound = origY - destY;
		} else {
			inbound = 0;
		}

		// outboundを求める
		if (dir == 0) {
			// 内回りの場合
			outbound = destY;
		} else if (origY < destY) {
			// 外回りで目的地のほうが外側にある場合
			outbound = destY - origY;
		} else {
			outbound = 0;
		}


		// これ以降、実際に経路をセットしていく
		int idx = 0;
		// 出発地を代入
		route[idx][X] = origX;
		route[idx][Y] = origY;

		for (int i = 0; i < inbound; i++) {
			// 方向
			route[idx][ISEC] = 1; // 上り方向
			idx++;
			// その結果たどり着く交差点
			route[idx][X] = route[idx-1][X];
			route[idx][Y] = route[idx-1][Y] - 1;
		}

		for (int i = 0; i < Math.abs(ring); i++) {
			// 方向
			route[idx][ISEC] = (ring > 0 ? 2 : 0); // 正回りor負回り
			idx++;
			// その結果たどり着く交差点
			route[idx][X] = (ring > 0 ?
					/*正回り*/ (route[idx-1][X]==numX-1 ? 0 : route[idx-1][X]+1) :
					/*負回り*/ (route[idx-1][X]==0 ? numX-1 : route[idx-1][X]-1));
			route[idx][Y] = route[idx-1][Y];
		}

		for (int i = 0; i < outbound; i++) {
			// 方向
			route[idx][ISEC] = 3; // 下り方向
			idx++;
			// その結果たどり着く交差点
			route[idx][X] = route[idx-1][X];
			route[idx][Y] = route[idx-1][Y] + 1;
		}

		// 最後の交差点を抜ける方向
		route[idx][ISEC] = destination[ISEC];

		// とりあえず最後にダミー値を入れておく。
		idx++;
		route[idx][X] = -1;
		route[idx][Y] = -1;
		route[idx][ISEC] = -1;
	}

	// 車を1サイト動かしたときに呼び出す。
	public void move(int newX, int newY, int newIsec, int newStep) {
		if (current[STEP] == 0 && newStep != 0) {
			// 交差点を抜けたとき
			routeStep++;

			if ((route[routeStep][X] != -1 && route[routeStep][X] != newX) ||
				(route[routeStep][Y] != -1 && route[routeStep][Y] != newX)) {
				throw new RuntimeException("車が経路通りの道順を進んでいません！");
			}
		}

		current[X]    = newX;
		current[Y]    = newY;
		current[ISEC] = newIsec;
		current[STEP] = newStep;
	}



	// 次の交差点で抜ける交差点番号を返す
	public int outIsec() {
		return route[routeStep][ISEC];
	}


	// 経路情報をコンソールに出力します。
	public void routeInfo() {
		for (int i = 0; ; i++) {
			if (route[i][0] == -1) break;
			System.out.print("(" + route[i][0] + "," + route[i][1] + ")");
		}
		System.out.println();
	}

	/**
	 * この車が消滅するか？
	 */
	public boolean tryDespawn() {
		if (current[X]    == destination[X] &&
			current[Y]    == destination[Y] &&
			current[ISEC] == destination[ISEC] &&
			current[STEP] == destination[STEP])
			return true;
		else
			return false;
	}

	/**
	 * 車を消滅させる直前に呼び出します。
	 */
	public void despawning() {
		System.out.println("車が消滅します");
	}
}
