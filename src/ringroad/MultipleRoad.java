package ringroad;

import java.awt.Color;

/**
 * 多車線道路
 *
 *  0 1 2 ...           (n-1)
 * ┌┬┬┬┬┬┬┬┬┬┬┐
 * ├┼┼┼┼┼┼┼┼┼┼┤
 * ├┼┼┼┼┼┼┼┼┼┼┤
 * └┴┴┴┴┴┴┴┴┴┴┘
 */
public class MultipleRoad extends Road {

	/*
	 * 道路サイトはQueue<Car>の配列として表す。
	 *
	 * 道路長は roadSites.length で取得する。
	 */
	private RoadSite[] roadSites;



	/**
	 * コンストラクタ
	 *
	 * @param thisX    この道路がある交差点のX座標
	 * @param thisY    この道路がある交差点のY座標
	 * @param thisIsec この道路の交差点番号
	 * @param length   この道路の長さ（交差点サイトを除いたサイト数）
	 * @param n        車線数
	 */
	public MultipleRoad(int thisX, int thisY, int thisIsec, int length, int n) {
		super(thisX, thisY, thisIsec, length);

		roadSites = new RoadSite[length];
		for (int i = 0; i < length; i++) {
			roadSites[i] = new RoadSite(thisX, thisY, thisIsec, i + 1, n);
		}
	}


	/**
	 * 指定された位置にいる車の台数を返す
	 * @param step 指定するサイト
	 */
	public int carsAt(int step) {
		return roadSites[step - 1].size();
	}

	private int numAlreadyLast; // この更新回で既に先頭にいる車の台数

	/**
	 * 内部サイトのアップデート
	 *
	 * 前進した車の数が返る
	 */
	@Override
	public int updateInternal() {
		int moved = 0;
		int length = roadSites.length;
		int[] empties = new int[length];

		numAlreadyLast = roadSites[length-1].size();

		// 現在の空き状況を取得する
		for (int i = 0; i < length; i++) {
			empties[i] = roadSites[i].emptySpace();
		}
		// 実際に車を移動させる
		for (int i = length-1; i >= 1; i--) {
			Car[] from = roadSites[i-1].dequeue(empties[i]);
			for (int j = 0; j < from.length; j++) {
				from[j].move(thisX, thisY, thisIsec, i + 1);
			}
			moved += from.length;
			roadSites[i].enqueue(from);
		}

		return moved;
	}


	/**
	 * 交差点から道路サイトへの車の移動を試みる
	 */
	@Override
	public boolean tryExit(Car car) {
		if (roadSites[0].emptySpace() > 0) {
			roadSites[0].enqueue(car);
			car.move(thisX, thisY, thisIsec, 1);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 道路サイトの出口から車を移動させる
	 * @param n 移動可能な最大数
	 */
	@Override
	public Car[] moveFromRoad(int n) {
		int num = Math.min(numAlreadyLast, n);
		return roadSites[roadSites.length-1].dequeue(num);
	}


	/**
	 * 車の発生を試みる。
	 */
	@Override
	public boolean trySpawn(int step) {
		return roadSites[step - 1].trySpawn();
	}


	/**
	 * 車の消滅を行なう。
	 */
	public int tryDespawn() {
		int deleted = 0;

		for (int i = 0; i < roadSites.length; i++) {
			deleted += roadSites[i].tryDespawn();
		}
		return deleted;
	}


	@Override
	public int getCarOut(int step) {
		// dummy
		return -1;
	}

	@Override
	public Color colorFunction(int step) {
		switch (roadSites[step - 1].size()) {
		case 0:
			return Color.WHITE;
		case 1:
			return Color.BLACK;
		case 2:
			return Color.MAGENTA;
		default: /* above 3 */
			return Color.RED;
		}
	}

}
