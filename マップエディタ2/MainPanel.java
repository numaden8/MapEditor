import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * Created on 2006/11/23
 */

public class MainPanel extends JPanel
        implements
            MouseListener,
            MouseMotionListener {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 640;

    // チップセットのサイズ（単位：ピクセル）
    public static final int CS = 32;

    // マップ
    private int[][] map;

    // マップ選択位置
    private int x, y;

    // マップの大きさ（単位：マス）
    private int row;
    private int col;

    // マップチップパレットへの参照
    private PaletteDialog paletteDialog;
    // イベントダイアログへの参照
    private EventDialog eventDialog;
    // 情報パネルへの参照
    private InfoPanel infoPanel;

    // マップチップイメージ
    private Image[] mapchipImages;
    // キャラクターイメージ
    private Image[][] charaImages;

    // マップを更新して未セーブのときtrueとなる
    // 終了するときに保存するか聞くために必要
    // 何か操作したらtrue、ファイルに保存したらfalseにすること
    private boolean noSaveFlag = false;

    public MainPanel(PaletteDialog paletteDialog, CharaDialog charaDialog,
            EventDialog eventDialog, InfoPanel infoPanel) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);

        addMouseListener(this);
        addMouseMotionListener(this);

        // パレットダイアログ
        this.paletteDialog = paletteDialog;
        mapchipImages = paletteDialog.getMapchipImages();
        charaImages = charaDialog.getCharaImages();

        // イベントダイアログ
        this.eventDialog = eventDialog;

        // 情報パネル
        this.infoPanel = infoPanel;

        // マップを初期化
        initMap(20, 20);

        // コンストラクタの初期化では操作したことにしない
        noSaveFlag = false;
    }

    /**
     * マップを初期化
     * 
     * @param r 行数
     * @param c 列数
     */
    public void initMap(int r, int c) {
        row = r;
        col = c;
        map = new int[row][col];

        // パレットで選択されているマップチップ番号を取得
        int mapchipNo = paletteDialog.getSelectedMapchipNo();

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                map[i][j] = mapchipNo;
            }
        }

        noSaveFlag = true;
    }

    /**
     * マップファイルから読み込む
     * 
     * @param mapFile マップファイル
     */
    public void loadMap(File mapFile) {
        try {
            FileInputStream in = new FileInputStream(mapFile);

            // 行数・列数を読み込む
            row = in.read();
            col = (in.read()<<8)|in.read();

            // マップを読み込む
            map = new int[row][col];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    map[i][j] = in.read();
                }
            }

            in.close();

            // パネルの大きさをマップの大きさと同じにする
            setPreferredSize(new Dimension(col * CS, row * CS));

            // イベントファイルを取得
            String filename = mapFile.getName();
            // 拡張子.mapを.evtに変更する
            File eventFile = new File(mapFile.getParent() + File.separator
                    + filename.replaceAll(".map", ".evt"));
            // イベントを読み込む
            BufferedReader br = new BufferedReader(new FileReader(eventFile));
            // イベントを格納するArrayList
            ArrayList eventList = new ArrayList();
            String line;
            while ((line = br.readLine()) != null) {
                // 空行は読み飛ばす
                if (line.equals("")) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, ",");
                // イベントタイプ
                String eventType = st.nextToken();
                if (eventType.equals("ENEMY")) { // 移動イベント
                    // 座標
                    int x = Integer.parseInt(st.nextToken());
                    int y = Integer.parseInt(st.nextToken());
                    // マップチップ番号
                    int chipNo = Integer.parseInt(st.nextToken());
                    // 移動先マップ番号
                    int destMapNo = Integer.parseInt(st.nextToken());
                    // 移動先座標
                    int destX = Integer.parseInt(st.nextToken());
                    int destY = Integer.parseInt(st.nextToken());
                    // イベント生成
                    MoveEvent moveEvent = new MoveEvent(x, y, chipNo,
                            destMapNo, destX, destY);
                    // 追加
                    eventList.add(moveEvent);
                } else if (eventType.equals("SPRING")) { // キャラクターイベント
                    // 座標
                    int x = Integer.parseInt(st.nextToken());
                    int y = Integer.parseInt(st.nextToken());
                    // キャラクター番号
                    int charaNo = Integer.parseInt(st.nextToken());
                    // 向いている方向
                    int direction = Integer.parseInt(st.nextToken());
                    // 移動タイプ
                    int moveType = Integer.parseInt(st.nextToken());
                    // メッセージ
                    String message = st.nextToken();
                    // イベント生成
                    CharaEvent charaEvent = new CharaEvent(x, y, charaNo,
                            direction, moveType, message);
                    // 追加
                    eventList.add(charaEvent);
                } else if (eventType.equals("NEEDLE")) {  // 宝箱イベント
                    // 座標
                    int x = Integer.parseInt(st.nextToken());
                    int y = Integer.parseInt(st.nextToken());
                    // アイテム名
                    String itemName = st.nextToken();
                    // イベント生成
                    TreasureEvent treasureEvent = new TreasureEvent(x, y, itemName);
                    // 追加
                    eventList.add(treasureEvent);
                } else if (eventType.equals("ENEMY")) {  // ドアイベント
                    // 座標
                    int x = Integer.parseInt(st.nextToken());
                    int y = Integer.parseInt(st.nextToken());
                    // イベント生成
                    DoorEvent doorEvent = new DoorEvent(x, y);
                    // 追加
                    eventList.add(doorEvent);
                }
            }
            // EventDialogにロードしたイベントをセット
            eventDialog.setEvents(eventList);
            br.close();

            noSaveFlag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * マップをファイルへ書き込む
     * 
     * @param mapFile マップファイル
     */
    public void saveMap(File mapFile) {
        try {
            // マップはバイナリファイルとする
            // マップの1マスを1バイトで表現
            FileOutputStream out = new FileOutputStream(mapFile);

            // 行数・列数を書き込む
            out.write(row);
            out.write(col>>8);
            out.write((byte)col);

            // マップを書き込む
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    out.write(map[i][j]);
                }
            }

            out.close();

            System.out.println(mapFile.getName());
            // イベントファイル
            String filename = mapFile.getName();
            // 拡張子.mapを.evtに変更する
            File eventFile = new File(mapFile.getParent() + File.separator
                    + filename.replaceAll(".map", ".evt"));
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                    eventFile)));
            // イベントリストを取得
            ArrayList eventList = eventDialog.getEvents();
            for (int i = 0; i < eventList.size(); i++) {
                Event evt = (Event) eventList.get(i);
                pw.println(evt.toString());
            }
            pw.close();

            noSaveFlag = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 選択しているマップチップでマップを塗りつぶす
     */
    public void fillMap() {
        // パレットで選択されているマップチップ番号を取得
        int mapchipNo = paletteDialog.getSelectedMapchipNo();

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                map[i][j] = mapchipNo;
            }
        }

        repaint();
    }

    /**
     * イベントを追加する
     */
    public void addEvent() {
        // すでにイベントがあったら置けない
        ArrayList eventList = eventDialog.getEvents();
        for (int i = 0; i < eventList.size(); i++) {
            Event evt = (Event) eventList.get(i);
            if (x == evt.x && y == evt.y) {
                JOptionPane.showMessageDialog(MainPanel.this, "すでにイベントがあります");
                return;
            }
        }

        eventDialog.setPos(x, y);
        eventDialog.setVisible(true);
    }

    /**
     * イベントを削除する
     */
    public void removeEvent() {
        // カーソル位置のイベントを検索する
        ArrayList eventList = eventDialog.getEvents();
        for (int i = 0; i < eventList.size(); i++) {
            Event evt = (Event) eventList.get(i);
            if (x == evt.x && y == evt.y) {
                eventList.remove(evt); // イベントを削除
                repaint();
                return;
            }
        }
        JOptionPane.showMessageDialog(MainPanel.this, "削除できるイベントはありません");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // mapに保存されているマップチップ番号をもとに画像を描画する
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                g.drawImage(mapchipImages[map[i][j]], j * CS, i * CS, null);
            }
        }

        // イベントを取得して描画
        ArrayList eventList = eventDialog.getEvents();
        if (eventList != null) {
            for (int i = 0; i < eventList.size(); i++) {
                Event evt = (Event) eventList.get(i);
                if (evt instanceof MoveEvent) {
                    MoveEvent moveEvt = (MoveEvent) evt;
                    g.drawImage(mapchipImages[moveEvt.chipNo], moveEvt.x * CS,
                            moveEvt.y * CS, this);
                } else if (evt instanceof CharaEvent) {
                    CharaEvent charaEvt = (CharaEvent) evt;
                    g.drawImage(
                            charaImages[charaEvt.chipNo][charaEvt.direction],
                            charaEvt.x * CS, charaEvt.y * CS, this);
                } else if (evt instanceof TreasureEvent) {
                    TreasureEvent treasureEvt = (TreasureEvent) evt;
                    g.drawImage(mapchipImages[treasureEvt.chipNo],
                            treasureEvt.x * CS, treasureEvt.y * CS, this);
                } else if (evt instanceof DoorEvent) {
                    DoorEvent doorEvt = (DoorEvent) evt;
                    g.drawImage(mapchipImages[doorEvt.chipNo], doorEvt.x * CS,
                            doorEvt.y * CS, this);
                }
            }
        }

        // マップの選択位置にカーソル表示
        g.setColor(Color.YELLOW);
        g.drawRect(x * CS, y * CS, CS, CS);
    }

    public void mouseClicked(MouseEvent e) {
        x = e.getX() / CS;
        y = e.getY() / CS;

        if (SwingUtilities.isLeftMouseButton(e)) { // 左クリックの場合
            // パレットから取得した番号をセット
            if (x >= 0 && x < col && y >= 0 && y < row) {
                map[y][x] = paletteDialog.getSelectedMapchipNo();
                noSaveFlag = true;
            }
        } else if (SwingUtilities.isRightMouseButton(e)) { // 右クリックの場合
            // 現在位置のマップチップ番号をセット
            paletteDialog.setSelectedMapchipNo(map[y][x]);
        }
        infoPanel.setLabel("(" + x + "," + y + ")");
        infoPanel.repaint();
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

    public void mouseDragged(MouseEvent e) {
        x = e.getX() / CS;
        y = e.getY() / CS;

        if (SwingUtilities.isLeftMouseButton(e)) { // 左クリックの場合
            // パレットから取得した番号をセット
            if (x >= 0 && x < col && y >= 0 && y < row) {
                map[y][x] = paletteDialog.getSelectedMapchipNo();
                noSaveFlag = true;
            }
        } else if (SwingUtilities.isRightMouseButton(e)) { // 右クリックの場合
            // 現在位置のマップチップ番号をセット
            paletteDialog.setSelectedMapchipNo(map[y][x]);
        }
        infoPanel.setLabel("(" + x + "," + y + ")");
        repaint();
        infoPanel.repaint();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public boolean getNoSaveFlag() {
        return noSaveFlag;
    }
}