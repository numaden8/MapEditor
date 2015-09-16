import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/*
 * Created on 2006/11/23
 */

public class MapEditor extends JFrame implements ActionListener {
    // マップの最小サイズ
    private static final int MIN_ROW = 20;
    private static final int MIN_COL = 20;

    // マップの最大サイズ
    private static final int MAX_ROW = 30;
    private static final int MAX_COL = 1024;

    // MapSizeDialogから取得した行数・列数
    private int row, col;

    // メニュー
    private JMenuItem newItem, saveItem, openItem, exitItem;
    private JMenuItem fillItem;
    private JMenuItem addEventItem, removeEventItem;
    private JMenuItem versionItem;

    // メインパネル
    private MainPanel mainPanel;
    private JScrollPane scrollPane;

    // ファイル選択ダイアログ（カレントディレクトリが始点）
    private JFileChooser fileChooser = new JFileChooser(".");

    public MapEditor() {
        setTitle("マップエディタ");
        setResizable(false);

        row = col = 20;
        initGUI();

        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * GUIを初期化
     */
    private void initGUI() {
        // パレットダイアログ
        PaletteDialog paletteDialog = new PaletteDialog(this);
        paletteDialog.setVisible(true);

        // キャラクターダイアログ
        CharaDialog charaDialog = new CharaDialog(this);
        charaDialog.setVisible(true);

        // イベントダイアログ
        EventDialog eventDialog = new EventDialog(this, paletteDialog,
                charaDialog);
        eventDialog.setVisible(false);

        // 情報パネル
        InfoPanel infoPanel = new InfoPanel();

        mainPanel = new MainPanel(paletteDialog, charaDialog, eventDialog, infoPanel);

        // イベントダイアログにメインパネルへの参照を渡す
        eventDialog.setMainPanel(mainPanel);

        // メインパネルをスクロールペインの上に乗せる
        scrollPane = new JScrollPane(mainPanel);
        
        Container contentPane = getContentPane();

        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(infoPanel, BorderLayout.NORTH);

        // ファイルメニュー
        JMenu fileMenu = new JMenu("ファイル");
        JMenu editMenu = new JMenu("編集");
        JMenu eventMenu = new JMenu("イベント");
        JMenu helpMenu = new JMenu("ヘルプ");

        newItem = new JMenuItem("新規作成");
        openItem = new JMenuItem("開く");
        saveItem = new JMenuItem("保存");
        exitItem = new JMenuItem("終了");
        fillItem = new JMenuItem("塗りつぶす");
        addEventItem = new JMenuItem("追加");
        removeEventItem = new JMenuItem("削除");

        versionItem = new JMenuItem("バージョン情報");

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator(); // 区切り
        fileMenu.add(exitItem);
        editMenu.add(fillItem);
        eventMenu.add(addEventItem);
        eventMenu.add(removeEventItem);
        helpMenu.add(versionItem);

        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);
        fillItem.addActionListener(this);
        addEventItem.addActionListener(this);
        removeEventItem.addActionListener(this);
        versionItem.addActionListener(this);

        // メニューバー
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(eventMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == newItem) {
            newMap();
        } else if (source == openItem) {
            openMap();
        } else if (source == saveItem) {
            saveMap();
        } else if (source == exitItem) {
            exit();
        } else if (source == fillItem) {
            mainPanel.fillMap();
        } else if (source == addEventItem) {
            mainPanel.addEvent();
        } else if (source == removeEventItem) {
            mainPanel.removeEvent();
        } else if (source == versionItem) {
            JOptionPane.showMessageDialog(MapEditor.this, "マップエディター Ver.2.0",
                    "バージョン情報", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /**
     * 新しいマップを作成する
     */
    public void newMap() {
        // MapSizeDialogを開く
        // rowとcolをセット
        MapSizeDialog dialog = new MapSizeDialog(this);

        // MapSizeDialogをMainPanelの中央に表示
        int dx = (MainPanel.WIDTH - dialog.getWidth()) / 2;
        int dy = (MainPanel.HEIGHT - dialog.getHeight()) / 2;
        dialog.setBounds(dx, dy, dialog.getWidth(), dialog.getHeight());

        dialog.setVisible(true);

        // ｷｬﾝｾﾙボタンが押されたときは何もしない
        if (!dialog.isOKPressed()) {
            return;
        }

        // メインパネルに新しいマップを作成
        mainPanel.initMap(row, col);

        // パネルの大きさをマップの大きさと同じにする
        mainPanel.setPreferredSize(new Dimension(col * MainPanel.CS, row
                * MainPanel.CS));
        // パネルが大きくなったらスクロールバーを表示
        scrollPane.getViewport().revalidate();
        scrollPane.getViewport().repaint();
    }

    /**
     * マップを開く
     */
    public void openMap() {
        fileChooser.addChoosableFileFilter(new MapFileFilter());
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setDialogTitle("マップを開く");

        int ret = fileChooser.showOpenDialog(null);

        File mapFile;
        if (ret == JFileChooser.APPROVE_OPTION) {
            // もし開くボタンが押されたらマップファイルをロードする
            mapFile = fileChooser.getSelectedFile();
            mainPanel.loadMap(mapFile);
            // パネルが大きくなったらスクロールバーを表示する
            scrollPane.getViewport().revalidate();
            scrollPane.getViewport().repaint();
        }
    }

    /**
     * マップを保存
     * 
     * @return ダイアログで押されたボタン
     */
    public int saveMap() {
        fileChooser.addChoosableFileFilter(new MapFileFilter());
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setDialogTitle("マップを保存する");

        int ret = fileChooser.showSaveDialog(null);

        File mapFile;
        if (ret == JFileChooser.APPROVE_OPTION) {
            // もし保存ボタンが押されたらマップファイルをセーブする
            mapFile = fileChooser.getSelectedFile();
            mainPanel.saveMap(mapFile);
        }

        return ret;
    }

    /**
     * プログラムを終了する
     */
    public void exit() {
        if (mainPanel.getNoSaveFlag() == true) {
            // 未セーブのときは警告ダイアログ
            int ret = JOptionPane.showConfirmDialog(MapEditor.this,
                    "変更を保存しますか?", "終了", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (ret == JOptionPane.YES_OPTION) { // はい
                int val = saveMap(); // ファイルを保存ダイアログ表示
                // もし保存したなら終了してよい
                // ダイアログでｷｬﾝｾﾙしたなら終了しない
                if (val == JFileChooser.APPROVE_OPTION) {
                    System.exit(0);
                }
            } else if (ret == JOptionPane.NO_OPTION) { // いいえ
                System.exit(0);
            }
        } else {
            // セーブ済みならそのまま終了
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new MapEditor();
    }

    private class MapSizeDialog extends JDialog implements ActionListener {
        private JTextField rowTextField;
        private JTextField colTextField;
        private JButton okButton;
        private JButton cancelButton;

        // OKボタンが押されたらtrue
        private boolean isOKPressed;

        public MapSizeDialog(JFrame parent) {
            super(parent, "マップ作成", true);

            isOKPressed = false;

            rowTextField = new JTextField(4);
            colTextField = new JTextField(4);
            okButton = new JButton("OK");
            cancelButton = new JButton("ｷｬﾝｾﾙ");
            okButton.addActionListener(this);
            cancelButton.addActionListener(this);

            JPanel p1 = new JPanel();
            p1.add(new JLabel("行数"));
            p1.add(rowTextField);

            JPanel p2 = new JPanel();
            p2.add(new JLabel("列数"));
            p2.add(colTextField);

            JPanel p3 = new JPanel();
            p3.add(okButton);
            p3.add(cancelButton);

            Container contentPane = getContentPane();
            contentPane.setLayout(new GridLayout(3, 1));
            contentPane.add(p1);
            contentPane.add(p2);
            contentPane.add(p3);

            pack();
        }

        public boolean isOKPressed() {
            return isOKPressed;
        }
        
        public static final int MAP_WIDTH_MAX = 1024;
        public static final int MAP_HEIGHT = 30;
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == okButton) {
                try {
                    // MapEditorクラスのインスタンス変数rowとcolを設定
                    row = Integer.parseInt(rowTextField.getText());
                    col = Integer.parseInt(colTextField.getText());

                    // マップの最低サイズ
                    if (row < 20 || col < 20) {
                        JOptionPane.showMessageDialog(MapSizeDialog.this,
                                "マップのサイズは" + MapEditor.MIN_ROW + "x"
                                        + MapEditor.MIN_COL + "以上にしてください");
                        row = col = 20;
                        return;
                    }

                    // マップの最大サイズ
                    if (row > 255 || col > MAP_WIDTH_MAX) {
                        JOptionPane.showMessageDialog(MapSizeDialog.this,
                                "マップのサイズは" + MapEditor.MAX_ROW + "x"
                                        + MapEditor.MAX_COL + "以下にしてください");
//                        row = col = 255;
                        row = 30;
                        col = MAP_WIDTH_MAX;
                        return;
                    }
                } catch (NumberFormatException ex) {
                    // テキストボックスに数値以外が入力されたとき
                    JOptionPane.showMessageDialog(MapSizeDialog.this,
                            "数値を入力してください");
                    rowTextField.setText("");
                    colTextField.setText("");
                    return;
                }

                isOKPressed = true;
                setVisible(false);
            } else if (e.getSource() == cancelButton) {
                isOKPressed = false;
                // ｷｬﾝｾﾙなら何もしない
                setVisible(false);
            }
        }
    }

    /**
     * マップファイルフィルタ（.mapファイルとフォルダだけ表示する）
     */
    private class MapFileFilter extends FileFilter {
        public boolean accept(File file) {
            // 拡張子を取得
            String extension = ""; // 拡張子
            if (file.getPath().lastIndexOf('.') > 0) {
                extension = file.getPath().substring(
                        file.getPath().lastIndexOf('.') + 1).toLowerCase();
            }

            // mapファイルかディレクトリだったらtrueを返す
            if (extension != "") {
                return extension.equals("map");
            } else {
                return file.isDirectory();
            }
        }

        public String getDescription() {
            return "Map Files (*.map)";
        }
    }
}
