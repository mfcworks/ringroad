package ringroad;

import java.awt.Color;

/**
 * ラウンドアバウト交差点を持つ交差点
 *
 */
public class Roundabout extends Intersection {

	// 交差点サイトのデータ
	// XXX: 暫定的に、交差点サイトは1車線としておく。
	private Car[] roundabout;

	// 道路サイトの配列
	private Road[] roads;



	// 隣接する交差点の配列
	// 交差点番号 i から伸びる道路サイトが接続する交差点を neighbors[i] に格納する。
	public Intersection[] neighbors;

	public Intersection neighbor(int isec) {
		return neighbors[isec];
	}



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
		neighbors = new Intersection[] {is0, is1, is2, is3}; // 順番通りに格納
	}

	/**
	 * 交差点を含まないサイト数を返す。
	 */
	public int lengthAt(int isec) {
		if (roads[isec] == null) {
			return 0;
		} else {
			return roads[isec].length;
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

	// 交差点から道路サイトへ抜ける車をアップデートする（自分の交差点内での操作）
	public int updateExit() {
		int moved = 0;

		for (int i = 0; i < 4; i++) {
			if (roundabout[i] != null && roundabout[i].outIsec() == i && !roundabout[i].isDespawn()) {
				if (roads[i].tryExit(roundabout[i])) {
					roundabout[i] = null;
					moved++;
				}
			}
		}

		return moved;
	}


	/**
	 * この交差点の交差点サイト内にいる車をアップデートする。
	 *
	 * ルール上、前の段階で交差点から道路へ抜ける車は全て
	 * 動かしてあるので、この段階で交差点に車がいる場合、
	 * 考えられるのは次の2通り。
	 * ・車が交差点を回る場合 (car.isec != isec)
	 * ・車が道路に抜けたいが、動けなかった場合 (car.isec == isec)
	 * 後者は動けないので動かさず、前者を動かす。
	 */
	public int updateIntersection() {
		// XXX: とりあえず以下の特殊ルールを適用しておく。
		// XXX: 2つの交差点と、それらの間の往復車線における全てのサイトに車が詰まるデッドロックが
		// 発生したため、外に抜けられない交差点内の車はラウンドアバウトを回るルールに変更する。
		boolean flag = true;
		for (int i = 0; i < 4; i++) {
			if (roundabout[i] == null /*|| roundabout[i].outIsec() == i*/) {
				flag = false;
				break;
			}
		}

		if (flag) {
			// 全ての交差点サイトに車がいる場合、無条件に（抜けられる場合は前段で抜けているはずなので）
			// それぞれをヌルっと動かす。
			Car temp = roundabout[3];
			for (int i = 3; i > 0; i--) {
				roundabout[i] = roundabout[i - 1];
				roundabout[i].move(thisX, thisY, i, 0);
			}
			roundabout[0] = temp;
			roundabout[0].move(thisX, thisY, 0, 0);
			return 4;
		} else {
			// 交差点サイトのどこかに空きがあるか、いずれかの車がスタックしている場合、
			// 交差点を回る車の前に車がいなければ動かす
			int moved = 0;
			for (int i = 0; i < 4; i++) {
				int next = (i + 1) % 4;
				if (roundabout[i] != null && roundabout[i].outIsec() != i && roundabout[next] == null) {
					roundabout[next] = roundabout[i];
					roundabout[next].move(thisX, thisY, next, 0);
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
		if (roads[isec] == null)
			return new Car[0];
		else
			return roads[isec].moveFromRoad(n);
	}

	/* 道路サイトからこの交差点サイトへ入る車をアップデートする。
	 *
	 * 交差点サイトに空きがある場合、隣接する交差点の指定する交差点番号の道路に
	 * 空き数の車を要求する。
	 */
	public int updateEnter() {
		int moved = 0;
		// 交差点番号 i に接続されているのはneighbors[i]の交差点番号(i+1)%4。←ここがおかしかった。(i+2)%4は間違い
		for (int i = 0; i < 4; i++) {
			int next = (i + 1) % 4;
			int prev = (i + 3) % 4;
			if (roundabout[i] == null && neighbors[prev] != null) {
				Car[] cars = neighbors[prev].moveFromRoad(next, 1);
				if (cars.length == 1) {
					roundabout[i] = cars[0];
					roundabout[i].move(thisX, thisY, i, 0);
					moved++;
				}
			}
		}
		return moved;
	}


	/**
	 * 車の発生を試みる。
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

	/**
	 * 車の消滅を行なう。
	 */
	public int tryDespawn() {
		int deleted = 0;
		// 交差点サイト
		for (int i = 0; i < 4; i++) {
			if (roundabout[i] != null && roundabout[i].isDespawn()) {
				Car carToDel = roundabout[i];
				roundabout[i] = null;
				carToDel.despawning();
				deleted++;
			}
		}
		// 道路サイト
		for (int i = 0; i < 4; i++) {
			if (roads[i] != null)
				deleted += roads[i].tryDespawn();
		}

		return deleted;
	}

	public int getCarOut(int isec, int step) {
		if (step == 0) {
			return roundabout[isec].outIsec();
		} else {
			return roads[isec].getCarOut(step);
		}
	}

	/**
	 * 色を取得する
	 */
	public Color getColor(int isec, int step) {
		if (step == 0) {
			return (roundabout[isec] == null ? Color.WHITE : Color.BLACK);
		} else {
			return roads[isec].colorFunction(step);
		}
	}

}
