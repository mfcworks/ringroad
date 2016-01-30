package ringroad;

/**
 * 多車線道路
 *
 *  0 1 2 ...           (n-1)
 * ┌┬┬┬┬┬┬┬┬┬┬┐
 * ├┼┼┼┼┼┼┼┼┼┼┤
 * ├┼┼┼┼┼┼┼┼┼┼┤
 * └┴┴┴┴┴┴┴┴┴┴┘
 */
public class MultipleRoad implements Road {

	/*
	 * 道路サイトはQueue<Car>の配列として表す。
	 *
	 * 道路長は roadSites.length で取得する。
	 */
	RoadSite[] roadSites;

	// 位置情報
	private int x;
	private int y;


	/**
	 * コンストラクタ
	 *
	 * @param length 道路長
	 * @param n      車線数
	 */
	public MultipleRoad(int x, int y, int length, int n) {
		this.x = x;
		this.y = y;

		roadSites = new RoadSite[length];
		for (int i = 0; i < length; i++) {
			roadSites[i] = new RoadSite(n);
		}
	}

	/**
	 * 道路長を返します。
	 */
	public int length() {
		return roadSites.length;
	}

	public int carsAt(int step) {
		return roadSites[step].size();
	}

	/**
	 * 内部サイトのアップデート
	 *
	 * 前進した車の数が返る
	 */
	public int updateInternal() {
		int moved = 0;
		int length = roadSites.length;
		int[] empties = new int[length];

		// 現在の空き状況を取得する
		for (int i = 0; i < length; i++) {
			empties[i] = roadSites[i].emptySpace();
		}
		// 実際に車を移動させる
		for (int i = 0; i < length - 1; i++) {
			Car[] from = roadSites[i].dequeue(empties[i+1]);
			moved += from.length;
			roadSites[i+1].enqueue(from);
		}

		return moved;
	}

	/**
	 * 交差点から道路サイトへの車の移動を試みる
	 */
	public boolean tryMoveToRoad(Car car) {
		if (roadSites[0].emptySpace() > 0) {
			roadSites[0].enqueue(car);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 道路サイトの出口から車を移動させる
	 */
	public Car[] moveFromRoad(int n) {
		return roadSites[roadSites.length-1].dequeue(n);
	}


	/**
	 * 車の発生を試みる
	 */
	public boolean trySpawn(int step) {
		return roadSites[step - 1].trySpawn();
	}



	/**
	 * 車の消滅
	 */
	public int despawn() {
		int num = 0;

		for (int i = 0; i < roadSites.length; i++) {
			num += roadSites[i].despawn();
		}

		return num;
	}
}
