package ringroad;

/**
 * 一車線道路
 *
 */
public class SingleRoad implements Road {

	/*
	 * この道路の位置情報
	 */
	private int x;
	private int y;
	private int isec;

	/*
	 * 道路サイトはCarの配列として表す。
	 * インデックスは 0 <= i <= length-1 であり、
	 * 進行方向にインデックスが増大する。
	 *
	 * 道路長は road.length で取得する。
	 */
	public Car[] road;

	/**
	 * コンストラクタ
	 *
	 * @param length 道路長
	 */
	public SingleRoad(int length) {
		road = new Car[length];
	}

	/**
	 * 道路長を取得します。
	 */
	public int length() {
		return road.length;
	}

	// 道路の入り口に何台入れるか
	public int spaceEnter() {
		return (road[0] == null ? 1 : 0);
	}


	/**
	 * 内部サイトのアップデート
	 */
	public int updateInternal() {
		int moved = 0; // 動いた台数

		for (int i = 0; i < road.length - 1; i++) {
			if (road[i] != null && road[i+1] == null) {
				road[i+1] = road[i];
				road[i] = null;
				i++;
				moved++;
			}
		}
		return moved;
	}


	/**
	 * 交差点から道路サイトへの車の移動を試みる
	 */
	public boolean tryMoveToRoad(Car car) {
		if (road[0] == null) {
			road[0] = car;
			return true;
		} else {
			return false;
		}
	}


	/**
	 * 道路サイトの出口から車を移動させる
	 * (SingleRoadの場合は最大1台)
	 */
	public Car[] moveFromRoad(int n) {
		int cap = (road[road.length-1] == null ? 0 : 1);
		int num = Math.min(cap, n); //移動する台数
		Car[] cars = new Car[num];
		if (num == 1) {
			cars[0] = road[road.length-1];
			road[road.length-1] = null;
		}
		return cars;
	}

	/*
	/ **
	 * 最大n台、交差点へ移動できる。
	 * 一車線道路なので、0 <= n <= 1 の必要がある。
	 * /
	public Car[] moveFrom(int n) {
		if (n > 1) {
			throw new IllegalArgumentException(
					"SingleRoadのインスタンスに1以上の移動台数が要求されました: " + n);
		}

		Car[] transfer;
		if (n == 1 && road[road.length-1] != null) {
			transfer = new Car[1];
			transfer[0] = road[road.length-1];
			road[road.length-1] = null;
		} else {
			transfer = new Car[0];
		}
		return transfer;
	}
	*/



	/**
	 * 車の発生を試みる
	 */
	public boolean trySpawn(int step) {
		if (road[step - 1] == null) {
			road[step - 1] = new Car();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 車の消滅
	 */
	public int despawn() {
		int num = 0;

		for (int i = 0; i < road.length; i++) {
			if (road[i].curPosX == x &&
				road[i].curPosY == y &&
				road[i].curIsec == isec &&
				// XXX: 注意：車の位置は配列のインデックスと 1 だけ異なる！
				road[i].curStep == i + 1) {
				// 車を消滅させる
				road[i] = null;
				num++;
			}
		}

		return num;
	}
}