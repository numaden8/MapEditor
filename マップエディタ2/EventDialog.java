import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * Created on 2006/12/02
 */

public class EventDialog extends JDialog {
    private static final int DOWN = 3;

    // イベント座標
    private int x, y;
    // イベントのマップチップ番号
    private int mapchipNo = 128;
    // キャラクター番号
    private int charaNo = 0;

    // 分割されたイメージ
    private Image[] mapchipImages;
    private Image[][] charaImages;

    // イベントリスト
    private ArrayList eventList = new ArrayList();

    // GUI部品
    private JLabel mapchipLabel;
    private JLabel coordLabel1;
    private JTextField destMapNoText;
    private JTextField destXText;
    private JTextField destYText;
    private JButton okButton;
    private JButton cancelButton;

    private JLabel charaLabel;
    private JLabel coordLabel2;
    private JComboBox directionBox;
    private JComboBox moveTypeBox;
    private JTextArea messageArea;

    private JLabel treasureLabel;
    private JLabel coordLabel3;
    private JTextField itemText;

    private JLabel doorLabel;
    private JLabel coordLabel4;

    // パレットダイアログへの参照
    private PaletteDialog paletteDialog;
    // キャラクターダイアログへの参照
    private CharaDialog charaDialog;
    // メインパネルへの参照
    private MainPanel mainPanel;

    public EventDialog(JFrame parent, PaletteDialog paletteDialog,
            CharaDialog charaDialog) {
        super(parent, "イベントダイアログ", false);
        this.paletteDialog = paletteDialog;
        this.charaDialog = charaDialog;

        // 分割されたマップチップイメージをパレットダイアログから取得
        mapchipImages = paletteDialog.getMapchipImages();
        charaImages = charaDialog.getCharaImages();

        // GUIを初期化
        initGUI();
        pack();
    }

    /**
     * GUIを初期化する
     */
    private void initGUI() {
        // タブペイン
        JTabbedPane tabbedPane = new JTabbedPane();

        // 移動イベントタブのパネル
        JPanel moveEventPanel = new JPanel();
        moveEventPanel.setLayout(new BorderLayout());

        JPanel p1 = new JPanel();
        // マップチップラベル
        mapchipLabel = new JLabel();
        mapchipLabel.setIcon(new ImageIcon(mapchipImages[mapchipNo]));
        mapchipLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // 選択されているマップチップをラベルにセットする
                mapchipNo = paletteDialog.getSelectedMapchipNo();
                mapchipLabel.setIcon(new ImageIcon(mapchipImages[mapchipNo]));
            }
        });
        p1.add(mapchipLabel);

        // 座標ラベル
        JPanel p2 = new JPanel();
        coordLabel1 = new JLabel("(0, 0)");
        p2.add(coordLabel1);

        JPanel p3 = new JPanel();
        p3.setLayout(new GridLayout(2, 1));
        p3.add(p1);
        p3.add(p2);

        JPanel p4 = new JPanel();
        p4.setLayout(new GridLayout(5, 2));

        // 移動先マップ番号
        destMapNoText = new JTextField(3);
        p4.add(new JLabel("移動先マップ番号"));
        p4.add(destMapNoText);

        // 移動先座標
        destXText = new JTextField(3);
        destYText = new JTextField(3);
        p4.add(new JLabel("移動先X"));
        p4.add(destXText);
        p4.add(new JLabel("移動先Y"));
        p4.add(destYText);

        JPanel p5 = new JPanel();
        p5.setLayout(new GridLayout(1, 2));
        // OK、キャンセルボタン
        okButton = new JButton("OK");
        cancelButton = new JButton("ｷｬﾝｾﾙ");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // イベントオブジェクトを作成
                MoveEvent evt;
                try {
                    evt = new MoveEvent(x, y, mapchipNo, Integer
                            .parseInt(destMapNoText.getText()), Integer
                            .parseInt(destXText.getText()), Integer
                            .parseInt(destYText.getText()));
                } catch (NumberFormatException ex) {
                    // テキストボックスに数値以外が入力されたとき
                    JOptionPane.showMessageDialog(EventDialog.this,
                            "数値を入力してください");
                    destMapNoText.setText("");
                    destXText.setText("");
                    destYText.setText("");
                    return;
                }
                // イベントリストに追加
                eventList.add(evt);
                setVisible(false);
                mainPanel.repaint();
                System.out.println("移動イベント追加: " + evt);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        p5.add(okButton);
        p5.add(cancelButton);

        moveEventPanel.add(p3, BorderLayout.NORTH);
        moveEventPanel.add(p4, BorderLayout.CENTER);
        moveEventPanel.add(p5, BorderLayout.SOUTH);

        tabbedPane.addTab("移動", moveEventPanel);

        // キャラクタイベントタブ内のパネル
        JPanel charaEventPanel = new JPanel();
        charaEventPanel.setLayout(new BorderLayout());

        JPanel p6 = new JPanel();
        charaLabel = new JLabel();
        charaLabel.setIcon(new ImageIcon(charaImages[0][DOWN]));
        charaLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // 選択されているキャラクターをラベルにセットする
                charaNo = charaDialog.getSelectedCharaNo();
                charaLabel.setIcon(new ImageIcon(charaImages[charaNo][DOWN]));
            }
        });
        p6.add(charaLabel);

        // 座標ラベル
        JPanel p7 = new JPanel();
        coordLabel2 = new JLabel("(0, 0)");
        p7.add(coordLabel2);

        JPanel p8 = new JPanel();
        p8.setLayout(new GridLayout(2, 1));
        p8.add(p6);
        p8.add(p7);

        JPanel p9 = new JPanel();
        p9.setLayout(new GridLayout(2, 2));

        // 向き
        directionBox = new JComboBox();
        directionBox.setEditable(false);
        directionBox.addItem("左向き");
        directionBox.addItem("右向き");
        directionBox.addItem("上向き");
        directionBox.addItem("下向き");
        directionBox.setSelectedIndex(DOWN); // デフォルトは下向き
        directionBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = directionBox.getSelectedIndex();
                // ラベルの絵の向きを変える
                charaLabel.setIcon(new ImageIcon(charaImages[charaNo][index]));
            }
        });
        p9.add(new JLabel("向き"));
        p9.add(directionBox);

        // 移動タイプ
        moveTypeBox = new JComboBox();
        moveTypeBox.setEditable(false);
        moveTypeBox.addItem("移動しない");
        moveTypeBox.addItem("ランダム移動");
        p9.add(new JLabel("移動タイプ"));
        p9.add(moveTypeBox);

        // メッセージ
        JPanel p10 = new JPanel();
        messageArea = new JTextArea();
        messageArea.setRows(5);
        messageArea.setColumns(18);
        messageArea.setLineWrap(true);
        messageArea.setText("ここにメッセージを入力してください。改行は\\n、改ページは\\f");
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(messageArea);
        p10.add(scrollPane);

        // OK、キャンセルボタン
        JPanel p11 = new JPanel();
        p11.setLayout(new GridLayout(1, 2));
        okButton = new JButton("OK");
        cancelButton = new JButton("ｷｬﾝｾﾙ");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // イベントオブジェクトを作成
                CharaEvent evt = new CharaEvent(x, y, charaNo, directionBox
                        .getSelectedIndex(), moveTypeBox.getSelectedIndex(),
                        messageArea.getText());
                // イベントリストに追加
                eventList.add(evt);
                setVisible(false);
                mainPanel.repaint();
                System.out.println("キャラクターイベント追加: " + evt);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        p11.add(okButton);
        p11.add(cancelButton);

        JPanel p12 = new JPanel();
        p12.setLayout(new BorderLayout());
        p12.add(p9, BorderLayout.NORTH);
        p12.add(p10, BorderLayout.CENTER);

        charaEventPanel.add(p8, BorderLayout.NORTH);
        charaEventPanel.add(p12, BorderLayout.CENTER);
        charaEventPanel.add(p11, BorderLayout.SOUTH);

        tabbedPane.add("キャラ", charaEventPanel);

        // 宝箱イベント
        JPanel treasureEventPanel = new JPanel();
        treasureEventPanel.setLayout(new BorderLayout());

        JPanel p13 = new JPanel();
        // マップチップラベル
        treasureLabel = new JLabel();
        treasureLabel.setIcon(new ImageIcon(mapchipImages[194])); // 宝箱は194番
        p13.add(treasureLabel);

        // 座標ラベル
        JPanel p14 = new JPanel();
        coordLabel3 = new JLabel("(0, 0)");
        p14.add(coordLabel3);

        // アイテム名
        JPanel p15 = new JPanel();
        itemText = new JTextField(16);
        p15.add(itemText);

        JPanel p16 = new JPanel();
        p16.setLayout(new GridLayout(6, 1));

        p16.add(p13);
        p16.add(p14);
        p16.add(p15);

        // OK、キャンセルボタン
        JPanel p17 = new JPanel();
        p17.setLayout(new GridLayout(1, 2));
        okButton = new JButton("OK");
        cancelButton = new JButton("ｷｬﾝｾﾙ");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // イベントオブジェクトを作成
                TreasureEvent evt = new TreasureEvent(x, y, itemText.getText());
                // イベントリストに追加
                eventList.add(evt);
                setVisible(false);
                mainPanel.repaint();
                System.out.println("宝箱イベント追加: " + evt);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        p17.add(okButton);
        p17.add(cancelButton);

        treasureEventPanel.add(p16, BorderLayout.CENTER);
        treasureEventPanel.add(p17, BorderLayout.SOUTH);

        tabbedPane.addTab("宝箱", treasureEventPanel);

        // ドアイベント
        JPanel doorEventPanel = new JPanel();
        doorEventPanel.setLayout(new BorderLayout());

        JPanel p18 = new JPanel();
        // マップチップラベル
        doorLabel = new JLabel();
        doorLabel.setIcon(new ImageIcon(mapchipImages[195])); // ドアは195番
        p18.add(doorLabel);

        // 座標ラベル
        JPanel p19 = new JPanel();
        coordLabel4 = new JLabel("(0, 0)");
        p19.add(coordLabel4);

        JPanel p20 = new JPanel();
        p20.setLayout(new GridLayout(6, 1));

        p20.add(p18);
        p20.add(p19);

        // OK、キャンセルボタン
        JPanel p21 = new JPanel();
        p21.setLayout(new GridLayout(1, 2));
        okButton = new JButton("OK");
        cancelButton = new JButton("ｷｬﾝｾﾙ");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // イベントオブジェクトを作成
                DoorEvent evt = new DoorEvent(x, y);
                // イベントリストに追加
                eventList.add(evt);
                setVisible(false);
                mainPanel.repaint();
                System.out.println("ドアイベント追加: " + evt);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        p21.add(okButton);
        p21.add(cancelButton);

        doorEventPanel.setLayout(new BorderLayout());
        doorEventPanel.add(p20, BorderLayout.CENTER);
        doorEventPanel.add(p21, BorderLayout.SOUTH);

        tabbedPane.addTab("ドア", doorEventPanel);

        getContentPane().add(tabbedPane);
    }

    /**
     * イベント座標をセット
     * 
     * @param x X座標
     * @param y Y座標
     */
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;

        coordLabel1.setText("(" + x + ", " + y + ")");
        coordLabel2.setText("(" + x + ", " + y + ")");
        coordLabel3.setText("(" + x + ", " + y + ")");
        coordLabel4.setText("(" + x + ", " + y + ")");
    }

    /**
     * イベントリストを返す
     * 
     * @return イベントリスト
     */
    public ArrayList getEvents() {
        return eventList;
    }

    /**
     * イベントリストをセット
     * 
     * @param eventList イベントリスト
     */
    public void setEvents(ArrayList eventList) {
        this.eventList = eventList;
    }

    /**
     * メインパネルへの参照をセット
     * 
     * @param mainPanel メインパネル
     */
    public void setMainPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }
}
