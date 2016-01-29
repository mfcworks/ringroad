package ringroad;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 放射環状道路の可視化クラス
 *
 */
public class FieldView extends JPanel {

	private JFrame frame;
	private Field field;

	public FieldView(int width, int height) {
		// メインウィンドウを作成
		frame = new JFrame("Ringroad Simulator");
		// 閉じるボタンで終了
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// ウィンドウ位置のデフォルト化
		frame.setLocationByPlatform(true);
		// サイズ変更不可
		frame.setResizable(false);
		// サイズ設定
		frame.setSize(width, height);

		// 描画用パネルを追加
		frame.add(this);

		// ウィンドウを表示
		frame.setVisible(true);
	}

	/**
	 * 現在の状態を描画する
	 *
	 * @param field 描画対象のField
	 */
	public void draw(Field field) {
		this.field = field;
		repaint();
	}

	// repaint()から呼び出される
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// test
		test(g, 0, 0, 0);

		if (field == null) return;

		// 描画の開始
		//

		// 背景
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, getWidth(), getHeight());
	}


	double R = 4; // ボールサイズ(半径)

	// 円を塗りつぶす：中心x、中心y、半径r
	void fillCircle(Graphics g, double x, double y, double r) {
		int xx = (int) Math.round(x - r);
		int yy = (int) Math.round(y - r);
		int rr = (int) Math.round(r * 2);
		g.fillOval(xx, yy, rr, rr);
	}

	public void test(Graphics g, int numX, int numY, int rc) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// 描画パネルの中心座標
		int cx = getWidth() / 2;
		int cy = getHeight() / 2;

		g.setColor(Color.BLACK);

		/*
		 * 描画の座標系
		 *
		 *    ┼──→Ｘ
		 *    │
		 *    ↓
		 *    Ｙ
		 */

		int n = 100;

		double radius = R * n / Math.PI; // 必要半径

		// 環状道路の内側車線を描画
		for (int i = 0; i < n; i++) {
			double x = (cx + radius * Math.cos(2*Math.PI*i/n));
			double y = (cy + radius * Math.sin(2*Math.PI*i/n));
			fillCircle(g, x, y, R);
		}
		radius += 2*R;
		// 環状道路の外側車線を描画
		for (int i = 0; i < n; i++) {
			double x = (cx + radius * Math.cos(2*Math.PI*i/n));
			double y = (cy + radius * Math.sin(2*Math.PI*i/n));
			fillCircle(g, x, y, R);
		}

		numY = 9;
		int dY = 10;
		// 放射道路を描画
		double rad = radius + 2*R;
		for (int numy = 0; numy < numY; numy++) {
			for (int dy = 0; dy < dY; dy++) {
				double x = cx + (rad + dy*2*R) * Math.cos(2*Math.PI*numy/numY);
				double y = cy + (rad + dy*2*R) * Math.sin(2*Math.PI*numy/numY);

				double rx = R * Math.sin(2*Math.PI*numy/numY);
				double ry = R * Math.cos(2*Math.PI*numy/numY);

				fillCircle(g, x-rx, y+ry, R);
				fillCircle(g, x+rx, y-ry, R);
			}
		}

	}


	// test
	public static void main(String[] args) {
		FieldView view;
		view = new FieldView(500, 500);
	}
}
