package ringroad;

/**
 * 道路サイトのためのインターフェース(実装上は抽象クラス)
 *
 */
public abstract class Road {

	/**
	 * この道路がある交差点のX座標
	 */
	public final int thisX;
	/**
	 * この道路がある交差点のY座標
	 */
	public final int thisY;
	/**
	 * この道路の交差点番号
	 */
	public final int thisIsec;
	/**
	 * この道路の長さ（交差点サイトを除いたサイト数）
	 */
	public final int length;


	/**
	 * コンストラクタ。道路サイトを作成する
	 *
	 * @param thisX    この道路がある交差点のX座標
	 * @param thisY    この道路がある交差点のY座標
	 * @param thisIsec この道路の交差点番号
	 * @param length   この道路の長さ（交差点サイトを除いたサイト数）
	 */
	public Road(int thisX, int thisY, int thisIsec, int length) {
		this.thisX = thisX;
		this.thisY = thisY;
		this.thisIsec = thisIsec;
		this.length = length;
	}


	//
	public abstract int carsAt(int step);

	/*
	 * 道路の内部サイトをアップデートするメソッド
	 */
	public abstract int updateInternal();

	/*
	 * 車を発生させることを試みる。
	 * @param step 道路サイトのステップ番号: step > 0
	 *             (∵ step == 0 は交差点サイトのため。)
	 * @return 発生できたかどうか
	 */
	public abstract boolean trySpawn(int step);

	/*
	 * 道路サイトの入口に車を1台移動することを試みる
	 */
	public abstract boolean tryExit(Car car);

	/*
	 * 道路サイトの出口から車を移動させる
	 */
	public abstract Car[] moveFromRoad(int n);

	public abstract int tryDespawn();

	public abstract int getCarOut(int step);
}
