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
	 * コンストラクタ
	 *
	 * @param rc   中心半径
	 * @param numX 放射道路の本数
	 * @param numY 各環状道路の車線数(内側から;int配列)
	 * @param dY   環状道路の1区間の長さ
	 */
	public GradualField(int rc, int numX, int[] numY, int dY) {

		Car.field = this;

		try {
			java.lang.reflect.Field f;
			f = getClass().getSuperclass().getDeclaredField("numX");
			f.setAccessible(true);
			f.set(this, numX);

			f = getClass().getSuperclass().getDeclaredField("numY");
			f.setAccessible(true);
			f.set(this, numY.length);

			f = getClass().getSuperclass().getDeclaredField("rc");
			f.setAccessible(true);
			f.set(this, rc);

			f = getClass().getSuperclass().getDeclaredField("dY");
			f.setAccessible(true);
			f.set(this, dY);




			intersections = new Intersection[numX][numY.length];

			int[] dX = new int[numY.length];

			// 交差点オブジェクトの生成
			for (int x = 0; x < numX; x++) {
				for (int y = 0; y < numY.length; y++) {
					// 交差点番号0,2方向は、同心円上の環状道路部なので同じ長さ。
					// 交差点番号1,3方向は、放射道路なので同じ長さ==dY。
					// ただし、最内側と最外側は片方の放射道路を持たない。その場合は0を与える。
					int n02 = (int) Math.round((rc + (dY * y)) * 2 * Math.PI / numX);
					int n1 = (y == 0 ? 0 : dY);
					int n3 = (y == numY.length-1 ? 0 : dY);

					dX[y] = n02;

					// 放射道路の車線数
					int m1 = (y == 0 ? 1 : numY[y-1]);
					int m3 = numY[y];

					// 環状道路のみ指定された車線とする。
					// 放射道路は全て1車線とする。
					intersections[x][y] = new Roundabout(x, y, n02, n1, n02, n3, numY[y], m1, numY[y], m3);
					siteCount += n02*numY[y]*2 + n1*m1 + n3*m3 + 4;

				//	if (x == 0) System.out.println("環状道路" + y + "の区間長: " + n02 + ", 車線数: " + numY[y]);
				}
			}

			f = getClass().getSuperclass().getDeclaredField("dX");
			f.setAccessible(true);
			f.set(this, dX);

			// 隣接する交差点のリンク
			for (int x = 0; x < numX; x++) {
				for (int y = 0; y < numY.length; y++) {
					intersections[x][y].connect(
							(x == 0 ? intersections[numX-1][y] : intersections[x-1][y]),
							(y == 0 ? null : intersections[x][y-1]),
							(x == numX-1 ? intersections[0][y] : intersections[x+1][y]),
							(y == numY.length-1 ? null : intersections[x][y+1]));
				}
			}

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int[] nums = {3, 2, 1, 1, 1};
		GradualField gf = new GradualField(15, 10, nums, 10);
	}
}
