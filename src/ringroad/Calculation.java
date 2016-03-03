package ringroad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * 数値計算を行ない、データを取得してCSVファイルに保存する
 */
public class Calculation {

	public static void main(String[] args) {
		// パラメータを設定する
		int rc = 10;
		int x = 10;
		int[] ys = {3,2,1,1};
		int dy = 5;
		double pn = 5.0;

		// 数値計算を100回繰り返す
		for (int i = 0; i < 100; i++) {
			int lastStep = simulate(rc, x, ys, dy, pn, i);
			System.out.println("last step: " + lastStep);
		}
	}

	/**
	 * シミュレーションを実行する
	 *
	 * @param rc    中心半径
	 * @param x     放射道路の本数
	 * @param ys    環状道路の車線数の配列
	 * @param dy    放射道路の1区間の長さ
	 * @param pn    1ステップあたりの発生台数
	 * @param times 繰り返す回数
	 * @return      ほぼ渋滞するまでのステップ数
	 */
	public static int simulate(int rc, int x, int[] ys, int dy, double pn, int times) {

		String y_str = "{";
		for (int y : ys) {
			y_str = y_str + y + ",";
		}
		y_str = y_str + "}";

		// 新規ファイル作成
		String cd = new File(".").getAbsoluteFile().getParent();
		String fileName = "Rc" + rc + "_x" + x + "_y" + y_str
				+ "_dy" + dy + "_pn" + pn + "[" + times + "].csv";
		String br = System.getProperty("line.separator");

		PrintWriter pw = null;
		try {
			// Windows上のExcelでファイルを開けるように、
			// Shift-JISでエンコードする
			pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(cd + "\\" + fileName), "Shift_JIS"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}

		pw.write("Step, Velocity, Density" + br);

		// Fieldをインスタンス化
		Field field = new GradualField(rc, x, ys, dy);
		field.setSpawnProbability(pn);

		int step = 0;
		try {
			for (step = 0; step < 10000; step++) {
				int moved = field.update();
				double dens = field.getDensity();
				double cars = field.carCount;
				pw.write(step + ", " + ((double) moved)/cars + ", " + dens + br);
			}
		} catch (RuntimeException e) {
		} finally {
			// ファイル保存
			pw.close();
		}
		System.out.println("終了 at step " + step);
		return step;
	}
}
