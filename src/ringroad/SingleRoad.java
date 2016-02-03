package ringroad;

/**
 * 一車線道路
 *
 */
public class SingleRoad extends Road {



	/*
	 * 道路サイトはCarの配列として表す。
	 * インデックスは 0 <= i <= length-1 であり、
	 * 進行方向にインデックスが増大する。
	 *
	 * 道路長は road.length で取得する。
	 * road[0]がstep=1に対応する。
	 */
	public Car[] road;


	/**
	 * コンストラクタ。道路サイトを作成する
	 *
	 * @param thisX    この道路がある交差点のX座標
	 * @param thisY    この道路がある交差点のY座標
	 * @param thisIsec この道路の交差点番号
	 * @param length   この道路の長さ（交差点サイトを除いたサイト数）
	 */
	public SingleRoad(int thisX, int thisY, int thisIsec, int length) {
		super(thisX, thisY, thisIsec, length);
//		System.out.println("SingleRoad " + length + " at (" + x + "," + y + ")");
		road = new Car[length];
	}



	// 道路の入り口に何台入れるか
	public int spaceEnter() {
		return (road[0] == null ? 1 : 0);
	}

	public int carsAt(int step) {
		return (road[step - 1] == null ? 0 : 1);
	}


	/**
	 * 内部サイトのアップデート
	 */
	public int updateInternal() {
		int moved = 0; // 動いた台数

		for (int i = 0; i < road.length - 1; i++) {
			if (road[i] != null && road[i+1] == null) {
				road[i+1] = road[i];
				road[i+1].move(thisX, thisY, thisIsec, i+2); //stepはroadのインデックスより1大きいため
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
	public boolean tryExit(Car car) {
		if (road[0] == null) {
			// 移動成功
			road[0] = car;
			road[0].move(thisX, thisY, thisIsec, 1);
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




	/**
	 * 車の発生を試みる。
	 */
	public boolean trySpawn(int step) {
		if (road[step - 1] == null) {
			road[step - 1] = new Car(thisX, thisY, thisIsec, step);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 車の消滅を行なう。
	 */
	public int tryDespawn() {
		int deleted = 0;

		for (int i = 0; i < road.length; i++) {
			if (road[i] != null && road[i].tryDespawn()) {
				road[i].despawning();
				road[i] = null;
				deleted++;
			}
		}
		return deleted;
	}
}
