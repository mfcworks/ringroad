package ringroad;

import java.awt.Color;

/**
 * 立体交差の交差点ルールを持つ交差点
 *
 */
public class Interchange extends Intersection {

	// 道路サイトのオブジェクト
	private Road[] roads;

	// この交差点のサイト
	private RoadSite interchange;


	/**
	 * コンストラクタ
	 *
	 * @param thisX, thisY : この交差点の座標(X, Y)
	 * @param len0, len1, len2, len3 : 各交差点番号に接続する道路の長さ
	 * @param n0, n1, n2, n3 : 各交差点番号に接続する道路の車線数
	 */
	public Interchange(int thisX, int thisY, int len0, int len1, int len2, int len3, int n0, int n1, int n2, int n3) {
		super(thisX, thisY);

		// 道路サイトのオブジェクトを生成
		roads = new Road[4];
		roads[0] = (len0 == 0 ? null : (n0 == 1 ? new SingleRoad(thisX, thisY, 0, len0) : new MultipleRoad(thisX, thisY, 0, len0, n0)));
		roads[1] = (len1 == 0 ? null : (n1 == 1 ? new SingleRoad(thisX, thisY, 1, len1) : new MultipleRoad(thisX, thisY, 1, len1, n1)));
		roads[2] = (len2 == 0 ? null : (n2 == 1 ? new SingleRoad(thisX, thisY, 2, len2) : new MultipleRoad(thisX, thisY, 2, len2, n2)));
		roads[3] = (len3 == 0 ? null : (n3 == 1 ? new SingleRoad(thisX, thisY, 3, len3) : new MultipleRoad(thisX, thisY, 3, len3, n3)));
		// この交差点のサイト
		interchange = new RoadSite(thisX, thisY, 0, 0, 0); //???????
	}

	@Override
	public int numCarsByPosition(int isec, int step) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	/**
	 * この交差点が持つ道路サイトの内部をアップデートする
	 */
	@Override
	public int updateRoadSites() {
		int moved = 0;
		for (int i = 0; i < 4; i++) {
			if (roads[i] != null)
				moved += roads[i].updateInternal();
		}
		return moved;
	}

	/**
	 * 交差点から道路サイトへ抜ける車をアップデートする
	 */
	@Override
	public int updateExit() {
		// <<<<<<<<<<<>>>>>>>>>>>>>>>>>
		int moved = 0;
		// TODO : ココらへん以下を実装！

	}

	@Override
	public int updateIntersection() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public int updateEnter() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public boolean trySpawn(int isec, int step) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public int tryDespawn() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public Car[] moveFromRoad(int isec, int n) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public int getCarOut(int isec, int step) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public Color getColor(int isec, int step) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}


	/**
	 * 立体交差用の道路サイト
	 */
	static class InterchangeSite extends RoadSite {

		public InterchangeSite(int thisX, int thisY, int thisIsec, int thisStep, int n) {
			super(thisX, thisY, thisIsec, thisStep, n);
		}

		@Override
		public int tryDespawn() {
			int num = 0;

			// remove時に下に詰められるので、上から処理する
			for (int i = list.size() - 1; i >= 0; i--) {
				Car car = list.get(i);
				int[] dest = car.getDestination();
				if (dest[0] == thisX && dest[1] == thisY && dest[3] == thisStep) {
					car.despawning();
					list.remove(i);
				}
			}
			return num;
		}
	}

}




