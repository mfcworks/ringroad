package ringroad;

/**
 * 道路サイトのためのインターフェース
 *
 */
public interface Road {

	// 道路長を返す
	public int length();

	//
	public int carsAt(int step);

	/*
	 * 道路の内部サイトをアップデートするメソッド
	 */
	public int updateInternal();

	/*
	 * 目的地についた車を消滅させるメソッド
	 */
	public int despawn();

	/*
	 * 車を発生させることを試みる。
	 * @param step 道路サイトのステップ番号: step > 0
	 *             (∵ step == 0 は交差点サイトのため。)
	 * @return 発生できたかどうか
	 */
	public boolean trySpawn(int step);

	/*
	 * 道路サイトの入口に車を1台移動することを試みる
	 */
	public boolean tryMoveToRoad(Car car);

	/*
	 * 道路サイトの出口から車を移動させる
	 */
	public Car[] moveFromRoad(int n);
}
