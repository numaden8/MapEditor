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
 * Created on 2006/12/02
 */

public class CharaDialog extends JDialog {
    public static final int WIDTH = 256;
    public static final int HEIGHT = 192;

    // チップセットのサイズ（単位：ピクセル）
    public static final int CS = 32;

    // キャラクター数
    private static final int NUM_CHARAS = 48;
    private static final int NUM_CHARAS_IN_ROW = 8;

    // キャラクターイメージ
    private Image charaImage;
    // キャラクターごとに分割したイメージ
    // charaImages[charaNo][direction]
    private Image[][] charaImages;

    // 選択されているキャラクター番号
    private int selectedCharaNo;

    public CharaDialog(JFrame parent) {
        super(parent, "キャラクターパレット", false);

        setBounds(650, PaletteDialog.HEIGHT + 30, WIDTH, HEIGHT);
        setResizable(false);

        CharaPanel charaPanel = new CharaPanel();

        getContentPane().add(charaPanel);

        pack();

        // キャラクターイメージをロード
        loadImage();
    }

    /**
     * 選択されているキャラクター番号を返す
     * 
     * @return 選択されているキャラクター番号
     */
    public int getSelectedCharaNo() {
        return selectedCharaNo;
    }

    /**
     * 分割されたキャラクターイメージを返す
     * 
     * @return 分割されたキャラクターイメージ
     */
    public Image[][] getCharaImages() {
        return charaImages;
    }

    private void loadImage() {
        ImageIcon icon = new ImageIcon(getClass()
                .getResource("image/chara.gif"));
        charaImage = icon.getImage();

        // キャラクターごとにイメージを分割
        charaImages = new Image[NUM_CHARAS][4];
        for (int i = 0; i < NUM_CHARAS; i++) {
            for (int j = 0; j < 4; j++) {
                charaImages[i][j] = createImage(CS, CS);
                int cx = (i % NUM_CHARAS_IN_ROW) * (CS * 2);
                int cy = (i / NUM_CHARAS_IN_ROW) * (CS * 4);
                Graphics g = charaImages[i][j].getGraphics();
                g.setColor(new Color(128, 0, 0));
                g.fillRect(0, 0, CS, CS);
                g.drawImage(charaImage, 0, 0, CS, CS, cx, cy + j * CS, cx + CS,
                        cy + j * CS + CS, null);
                g.dispose();
            }
        }
    }

    private class CharaPanel extends JPanel implements MouseListener {
        // キャラクターの向き（下向きで固定）
        private int direction = 3;

        public CharaPanel() {
            setPreferredSize(new Dimension(CharaDialog.WIDTH,
                    CharaDialog.HEIGHT));
            setFocusable(true);

            addMouseListener(this);
        }

        public void paintComponent(Graphics g) {
            g.setColor(new Color(128, 0, 0));
            g.fillRect(0, 0, CharaDialog.WIDTH, CharaDialog.HEIGHT);

            // キャラクターイメージを描画
            for (int i = 0; i < NUM_CHARAS; i++) {
                int c = (i % NUM_CHARAS_IN_ROW);
                int r = (i / NUM_CHARAS_IN_ROW);
                // i番目のキャラクターの座標
                int cx = (i % NUM_CHARAS_IN_ROW) * (CS * 2);
                int cy = (i / NUM_CHARAS_IN_ROW) * (CS * 4);
                g.drawImage(charaImage, c * CS, r * CS, c * CS + CS, r * CS
                        + CS, cx, cy + direction * CS, cx + CS, cy + direction
                        * CS + CS, null);
            }

            // 選択されているキャラクターを枠で囲む
            int x = selectedCharaNo % NUM_CHARAS_IN_ROW;
            int y = selectedCharaNo / NUM_CHARAS_IN_ROW;
            g.setColor(Color.YELLOW);
            g.drawRect(x * CS, y * CS, CS, CS);
        }

        public void mouseClicked(MouseEvent e) {
            int x = e.getX() / CS;
            int y = e.getY() / CS;

            // キャラクター番号は左上から0, 1, 2と数える
            int charaNo = y * NUM_CHARAS_IN_ROW + x;
            if (charaNo > NUM_CHARAS) {
                charaNo = NUM_CHARAS;
            }

            selectedCharaNo = charaNo;

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
