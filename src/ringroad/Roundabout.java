package ringroad;

/**
 * ラウンドアバウト交差点を持つ交差点
 *
 */
public class Roundabout implements Intersection {

	// 道路サイトの配列
	private Road[] roads;

	// 隣接する交差点の配列
	// 交差点番号 i から伸びる道路サイトが接続する交差点を neighbors[i] に格納する。
	public Intersection[] neighbors;

	public Intersection[] neighbors() {
		return neighbors;
	}
	// 交差点サイトのデータ
	// XXX: 暫定的に、交差点サイトは1車線としておく。
	private Car[] roundabout;

	// この交差点の位置情報
	private int x;
	private int y;

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	/**
	 * コンストラクタ
	 *
	 * @param len0, len1, len2, len3 : 各交差点番号に接続する道路の長さ
	 * @param n0, n1, n2, n3 : 各交差点番号に接続する道路の車線数
	 */
	public Roundabout(int len0, int len1, int len2, int len3, int n0, int n1, int n2, int n3) {
		// 道路サイトのオブジェクトを生成
		roads = new Road[4];
		roads[0] = (len0 == 0 ? null : (n0 == 1 ? new SingleRoad(len0) : new MultipleRoad(len0, n0)));
		roads[1] = (len1 == 0 ? null : (n1 == 1 ? new SingleRoad(len1) : new MultipleRoad(len1, n1)));
		roads[2] = (len2 == 0 ? null : (n2 == 1 ? new SingleRoad(len2) : new MultipleRoad(len2, n2)));
		roads[3] = (len3 == 0 ? null : (n3 == 1 ? new SingleRoad(len3) : new MultipleRoad(len3, n3)));
	}

	/**
	 * この交差点の位置情報を覚えておく
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * 隣接する交差点を設定する
	 * @param is0 この交差点の交差点番号0から伸びる道路サイトの先に接続する交差点
	 * @param is1 この交差点の交差点番号1から伸びる道路サイトの先に接続する交差点
	 * @param is2 この交差点の交差点番号2から伸びる道路サイトの先に接続する交差点
	 * @param is3 この交差点の交差点番号3から伸びる道路サイトの先に接続する交差点
	 */
	public void connect(Intersection is0, Intersection is1, Intersection is2, Intersection is3) {
		neighbors[0] = is0;
		neighbors[1] = is1;
		neighbors[2] = is2;
		neighbors[3] = is3;
	}

	public int lengthAt(int isec) {
		if (roads[isec] == null) {
			return 0;
		} else {
			return roads[isec].length();
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
			moved += roads[i].updateInternal();
		}
		return moved;
	}

	// 交差点から道路サイトへ抜ける車をアップデートする
	public int updateExit() {
		int moved = 0;

		for (int i = 0; i < 4; i++) {
			if (roundabout[i] != null) {
				if (roundabout[i].outIsec() == i) {
					if (roads[i].tryMoveToRoad(roundabout[i])) {
						roundabout[i] = null;
						moved++;
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
			if (roundabout[isec] == null) {
				roundabout[isec] = new Car(x, y, isec, 0);
				return true;
			} else {
				return false;
			}
		} else {
			return roads[isec].trySpawn(step);
		}
	}

}
