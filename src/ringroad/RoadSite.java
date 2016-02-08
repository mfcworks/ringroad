package ringroad;

import java.util.LinkedList;

/**
 * 複数車線道路のための１サイトの車のQueue。
 *
 */
public class RoadSite {

	/*
	 * この道路サイトの位置情報
	 */
	protected final int thisX;
	protected final int thisY;
	protected final int thisIsec;
	protected final int thisStep;

	// Queueに入っている現在の車数はlist.size()で取得
	protected LinkedList<Car> list;

	// Queueの容量(最大車数)
	private final int nMax;

	/**
	 * コンストラクタ
	 *
	 * @param n Queueの容量(N車線分に相当)
	 */
	public RoadSite(int thisX, int thisY, int thisIsec, int thisStep, int n) {
		this.thisX = thisX;
		this.thisY = thisY;
		this.thisIsec = thisIsec;
		this.thisStep = thisStep;
		nMax = n;
		list = new LinkedList<Car>();
	}


	// 空き容量(車数)を取得する
	public int emptySpace() {
		return (nMax - list.size());
	}

	// 車の台数
	public int size() {
		return list.size();
	}

	//====================
	// 車の移動
	//====================


	// 車の追加
	public void enqueue(Car car) {
		enqueue(new Car[]{ car });
	}

	// 複数台の車の追加

	/**
	 * 移動してくる車をキューに追加する
	 * @param carsToMove 移動してくるCarの配列
	 */
	public void enqueue(Car[] carsToMove) {
		for (int i = 0; i < carsToMove.length; i++) {
			if (list.size() == nMax)
				throw new RuntimeException("Queueが一杯です");
			list.add(carsToMove[i]);
		}
	}


	/**
	 * 移動させる車をキューから取り除く
	 * @param nEmpties 移動できる最大数
	 * @return 移動するCarの配列
	 */
	public Car[] dequeue(int nEmpties) {
		int n = Math.min(nEmpties, list.size());
		Car[] carsToMove = new Car[n];

		for (int i = 0; i < n; i++) {
			if (list.size() == 0)
				throw new RuntimeException("空のQueueにdequeueが呼ばれました");
			carsToMove[i] = list.removeFirst();
		}
		return carsToMove;
	}


	//====================
	// 発生と消滅
	//====================

	// 車の発生を試みる
	public boolean trySpawn() {
		// listが満杯でなければ車を発生させる
		if (list.size() != nMax) {
			list.add(new Car(thisX, thisY, thisIsec, thisStep));
			return true;
		} else {
			return false;
		}
	}


	// 車を消滅させる
	public int tryDespawn() {
		int num = 0;

		// remove時に下に詰められるので、上から処理する
		for (int i = list.size() - 1; i >= 0; i--) {
			Car car = list.get(i);
			if (car.isDespawn()) {
				// 車を消滅させる
				car.despawning();
				list.remove(i);
				num++;
			}
		}
		return num;
	}
}
