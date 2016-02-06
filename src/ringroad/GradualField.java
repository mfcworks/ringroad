package ringroad;

/**
 * 中心部に向かうに従って環状道路の車線数が増えていく
 * タイプの放射環状道路。
 * (このクラス名はどうかと思うが)
 *
 * intの配列で環状道路数ならびに車線数を指定する。
 *
 */
public class GradualField extends Field {

	/**
	 * 交差点の2次元配列
	 */
	private Intersection[][] intersections;

	/**
	 * 中心半径
	 */
	public final int rc;
	/**
	 * 放射道路の本数
	 */
	public final int numX;
	/**
	 * 環状道路の本数
	 */
	public final int numY;
	/**
	 * 放射道路の1区間の長さ
	 */
	public final int dY;
	/**
	 * 環状道路の1区間の長さ
	 */
	public final int[] dX;


	public GradualField() {
		rc = numX = numY = dY = 4;
		dX = null;

	}

	public static void main(String[] args) {
		GradualField gf = new GradualField();
		gf.test();
	}
}
