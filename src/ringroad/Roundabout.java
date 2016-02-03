package ringroad;

/**
 * ラウンドアバウト交差点を持つ交差点
 *
 */
public class Roundabout extends Intersection {

	// 道路サイトの配列
	private Road[] roads;

	// 隣接する交差点の配列
	// 交差点番号 i から伸びる道路サイトが接続する交差点を neighbors[i] に格納する。
	public Intersection[] neighbors;

	public Intersection neighbor(int isec) {
		return neighbors[isec];
	}
	// 交差点サイトのデータ
	// XXX: 暫定的に、交差点サイトは1車線としておく。
	private Car[] roundabout;



	public int numCarsByPosition(int isec, int step) {
		if (step == 0)
			return (roundabout[isec] == null ? 0 : 1);
		else
			return roads[isec].carsAt(step);
	}

	/**
	 * コンストラクタ
	 *
	 * @param thisX, thisY : この交差点の座標(X, Y)
	 * @param len0, len1, len2, len3 : 各交差点番号に接続する道路の長さ
	 * @param n0, n1, n2, n3 : 各交差点番号に接続する道路の車線数
	 */
	public Roundabout(int thisX, int thisY, int len0, int len1, int len2, int len3, int n0, int n1, int n2, int n3) {
		super(thisX, thisY);

		// 道路サイトのオブジェクトを生成
		roads = new Road[4];
		roads[0] = (len0 == 0 ? null : (n0 == 1 ? new SingleRoad(thisX, thisY, 0, len0) : new MultipleRoad(thisX, thisY, 0, len0, n0)));
		roads[1] = (len1 == 0 ? null : (n1 == 1 ? new SingleRoad(thisX, thisY, 1, len1) : new MultipleRoad(thisX, thisY, 1, len1, n1)));
		roads[2] = (len2 == 0 ? null : (n2 == 1 ? new SingleRoad(thisX, thisY, 2, len2) : new MultipleRoad(thisX, thisY, 2, len2, n2)));
		roads[3] = (len3 == 0 ? null : (n3 == 1 ? new SingleRoad(thisX, thisY, 3, len3) : new MultipleRoad(thisX, thisY, 3, len3, n3)));
		// この交差点のサイト
		roundabout = new Car[4];
	}

	/**
	 * 隣接する交差点を設定する
	 * @param is0 この交差点の交差点番号0から伸びる道路サイトの先に接続する交差点
	 * @param is1 この交差点の交差点番号1から伸びる道路サイトの先に接続する交差点
	 * @param is2 この交差点の交差点番号2から伸びる道路サイトの先に接続する交差点
	 * @param is3 この交差点の交差点番号3から伸びる道路サイトの先に接続する交差点
	 */
	public void connect(Intersection is0, Intersection is1, Intersection is2, Intersection is3) {
		neighbors = new Intersection[4];
		neighbors[0] = is0;
		neighbors[1] = is1;
		neighbors[2] = is2;
		neighbors[3] = is3;
	}

	/**
	 * 交差点を含んでサイト数を返す。
	 * 例えば dY = 5 ならば、交差点サイトを1含むので6が返る。
	 * roadがnullなら1が返る。
	 */
	public int lengthAt(int isec) {
		if (roads[isec] == null) {
			return 1;
		} else {
			return roads[isec].length + 1;
		}
	}

	/**
	 * この交差点が持つ道路サイトの内部をアップデートする
	 *
	 * @return 動いた車の数
	 */
	public int updateRoadSites() {
		int moved = 0;
		for (int i = 0; i < 4; i++) {
			if (roads[i] != null)
				moved += roads[i].updateInternal();
		}
		return moved;
	}

	// 交差点から道路サイトへ抜ける車をアップデートする
	public int updateExit() {
		int moved = 0;

		for (int i = 0; i < 4; i++) {
			if (roundabout[i] != null) {
				if (roundabout[i].curPosX != thisX || roundabout[i].curPosY != thisY) {
					throw new RuntimeException("assertion error.");
				}
				if (roundabout[i].outIsec() == i) {
					if (roads[i].tryMoveToRoad(roundabout[i])) {
						roundabout[i] = null;
						moved++;
						System.out.println("交差点から道路サイトへ抜けた");
					}
				}
			}
		}

		return moved;
	}

	/**
	 * この交差点の交差点サイトにいる車をアップデートする。
	 *
	 * ルール上、前の段階で交差点から道路へ抜ける車は全て
	 * 動かしてあるので、この段階で交差点に車がいる場合、
	 * 考えられるのは次の2通り。
	 * ・車が交差点を回る場合 (car.isec != isec)
	 * ・車が道路に抜けたいが、動けなかった場合 (car.isec == isec)
	 * 後者は動けないので動かさず、前者を動かす。
	 */
	public int update() {
		// XXX: とりあえず以下の特殊ルールを適用しておく。
		boolean flag = true;
		for (int i = 0; i < 4; i++) {
			if (roundabout[i] == null || roundabout[i].outIsec() == i) {
				flag = false;
				break;
			}
		}

		if (flag) {
			// 全ての交差点サイトに車がいて、それぞれが交差点を回る場合、
			// それぞれをヌルっと動かす。
			Car temp = roundabout[3];
			for (int i = 3; i > 0; i--) {
				roundabout[i] = roundabout[i - 1];
			}
			roundabout[0] = temp;
			return 4;
		} else {
			// 交差点サイトのどこかに空きがあるか、いずれかの車がスタックしている場合、
			// 交差点を回る車の前に車がいなければ動かす
			int moved = 0;
			for (int i = 0; i < 4; i++) {
				int next = (i + 1) % 4;
				if (roundabout[i] != null && roundabout[i].outIsec() != i && roundabout[next] == null) {
					roundabout[next] = roundabout[i];
					roundabout[i] = null;
					System.out.println("交差点を回った");
					moved++;
				}
			}
			return moved;
		}
	}


	/**
	 * 指定された交差点番号における道路の出口から車を移動させる
	 */
	public Car[] moveFromRoad(int isec, int n) {
		return roads[isec].moveFromRoad(n);
	}

	/* 道路サイトからこの交差点サイトへ入る車をアップデートする。
	 *
	 * 交差点サイトに空きがある場合、隣接する交差点の指定する交差点番号の道路に
	 * 空き数の車を要求する。
	 */
	public int updateEnter() {
		int moved = 0;
		// 交差点番号 i に接続されているのはneighbors[i]の交差点番号(i+2)%4。
		for (int i = 0; i < 4; i++) {
			if (roundabout[i] == null) {
				Car[] cars = neighbors[i].moveFromRoad((i+2) % 4, 1);
				if (cars.length == 1) {
					roundabout[i] = cars[0];
					moved++;
				}
			}
		}
		return moved;
	}


	/**
	 * 車の発生を試みる
	 */
	public boolean trySpawn(int isec, int step) {
		if (step == 0) {
			// この交差点の交差点サイトに発生を試みる場合、
			// そこに既に車がいなければ発生させる。
			if (roundabout[isec] == null) {
				roundabout[isec] = new Car(thisX, thisY, isec, 0);
				return true;
			} else {
				return false;
			}
		} else {
			// 道路サイトに発生させる場合、道路サイトのメソッドへ投げる
			return roads[isec].trySpawn(step);
		}
	}

}
