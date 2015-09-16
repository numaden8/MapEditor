import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * Created on 2006/11/23
 */

public class PaletteDialog extends JDialog {
    public static final int WIDTH = 512;
    public static final int HEIGHT = 512;

    // チップセットのサイズ（単位：ピクセル）
    public static final int CS = 32;

    // チップ数
    private static final int NUM_CHIPS = 256;
    private static final int NUM_CHIPS_IN_ROW = 16;

    // マップチップイメージ
    private Image mapchipImage;
    // マップチップをチップごとに分割したイメージ
    private Image[] mapchipImages;

    // 選択されているマップチップ番号
    private int selectedMapchipNo;

    public PaletteDialog(JFrame parent) {
        // モードレスダイアログ
        super(parent, "マップチップパレット", false);

        setBounds(650, 0, WIDTH, HEIGHT);
        setResizable(false);

        PalettePanel palettePanel = new PalettePanel();
        getContentPane().add(palettePanel);

        pack();

        // マップチップイメージをロード
        loadImage();
    }

    /**
     * 選択されているマップチップ番号を返す
     * 
     * @return 選択されているマップチップ番号
     */
    public int getSelectedMapchipNo() {
        return selectedMapchipNo;
    }

    /**
     * マップチップ番号をセット
     * 
     * @param no マップチップ番号
     */
    public void setSelectedMapchipNo(int no) {
        selectedMapchipNo = no;
        repaint();
    }

    /**
     * 分割されたマップチップイメージを返す
     * 
     * @return 分割されたマップチップイメージ
     */
    public Image[] getMapchipImages() {
        return mapchipImages;
    }

    // マップチップイメージをロード
    private void loadImage() {
        ImageIcon icon = new ImageIcon(getClass().getResource(
                "image/mapchip.png"));
        mapchipImage = icon.getImage();

        // マップチップごとにイメージを分割
        mapchipImages = new Image[NUM_CHIPS];
        for (int i = 0; i < NUM_CHIPS; i++) {
            mapchipImages[i] = createImage(CS, CS);
            int x = i % NUM_CHIPS_IN_ROW;
            int y = i / NUM_CHIPS_IN_ROW;
            Graphics g = mapchipImages[i].getGraphics();
            g.setColor(new Color(0, 0, 0));
            g.fillRect(0, 0, CS, CS);
            g.drawImage(mapchipImage, 0, 0, CS, CS, x * CS, y * CS,
                    x * CS + CS, y * CS + CS, null);
            g.dispose();
        }
    }

    private class PalettePanel extends JPanel implements MouseListener {
        public PalettePanel() {
            setPreferredSize(new Dimension(PaletteDialog.WIDTH,
                    PaletteDialog.HEIGHT));
            setFocusable(true);

            addMouseListener(this);
        }

        public void paintComponent(Graphics g) {
            g.setColor(new Color(128, 0, 0));
            g.fillRect(0, 0, PaletteDialog.WIDTH, PaletteDialog.HEIGHT);

            // マップチップイメージを描画
            g.drawImage(mapchipImage, 0, 0, this);

            // 選択されているマップチップを枠で囲む
            int x = selectedMapchipNo % NUM_CHIPS_IN_ROW;
            int y = selectedMapchipNo / NUM_CHIPS_IN_ROW;
            g.setColor(Color.YELLOW);
            g.drawRect(x * CS, y * CS, CS, CS);
        }

        public void mouseClicked(MouseEvent e) {
            int x = e.getX() / CS;
            int y = e.getY() / CS;

            // マップチップ番号は左上から0, 1, 2と数える
            int mapchipNo = y * NUM_CHIPS_IN_ROW + x;
            if (mapchipNo > NUM_CHIPS) {
                mapchipNo = NUM_CHIPS;
            }

            selectedMapchipNo = mapchipNo;

            repaint();
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }
    }
}
