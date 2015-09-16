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

    // �`�b�v�Z�b�g�̃T�C�Y�i�P�ʁF�s�N�Z���j
    public static final int CS = 32;

    // �}�b�v
    private int[][] map;

    // �}�b�v�I���ʒu
    private int x, y;

    // �}�b�v�̑傫���i�P�ʁF�}�X�j
    private int row;
    private int col;

    // �}�b�v�`�b�v�p���b�g�ւ̎Q��
    private PaletteDialog paletteDialog;
    // �C�x���g�_�C�A���O�ւ̎Q��
    private EventDialog eventDialog;
    // ���p�l���ւ̎Q��
    private InfoPanel infoPanel;

    // �}�b�v�`�b�v�C���[�W
    private Image[] mapchipImages;
    // �L�����N�^�[�C���[�W
    private Image[][] charaImages;

    // �}�b�v���X�V���Ė��Z�[�u�̂Ƃ�true�ƂȂ�
    // �I������Ƃ��ɕۑ����邩�������߂ɕK�v
    // �������삵����true�A�t�@�C���ɕۑ�������false�ɂ��邱��
    private boolean noSaveFlag = false;

    public MainPanel(PaletteDialog paletteDialog, CharaDialog charaDialog,
            EventDialog eventDialog, InfoPanel infoPanel) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);

        addMouseListener(this);
        addMouseMotionListener(this);

        // �p���b�g�_�C�A���O
        this.paletteDialog = paletteDialog;
        mapchipImages = paletteDialog.getMapchipImages();
        charaImages = charaDialog.getCharaImages();

        // �C�x���g�_�C�A���O
        this.eventDialog = eventDialog;

        // ���p�l��
        this.infoPanel = infoPanel;

        // �}�b�v��������
        initMap(20, 20);

        // �R���X�g���N�^�̏������ł͑��삵�����Ƃɂ��Ȃ�
        noSaveFlag = false;
    }

    /**
     * �}�b�v��������
     * 
     * @param r �s��
     * @param c ��
     */
    public void initMap(int r, int c) {
        row = r;
        col = c;
        map = new int[row][col];

        // �p���b�g�őI������Ă���}�b�v�`�b�v�ԍ����擾
        int mapchipNo = paletteDialog.getSelectedMapchipNo();

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                map[i][j] = mapchipNo;
            }
        }

        noSaveFlag = true;
    }

    /**
     * �}�b�v�t�@�C������ǂݍ���
     * 
     * @param mapFile �}�b�v�t�@�C��
     */
    public void loadMap(File mapFile) {
        try {
            FileInputStream in = new FileInputStream(mapFile);

            // �s���E�񐔂�ǂݍ���
            row = in.read();
            col = (in.read()<<8)|in.read();

            // �}�b�v��ǂݍ���
            map = new int[row][col];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    map[i][j] = in.read();
                }
            }

            in.close();

            // �p�l���̑傫�����}�b�v�̑傫���Ɠ����ɂ���
            setPreferredSize(new Dimension(col * CS, row * CS));

            // �C�x���g�t�@�C�����擾
            String filename = mapFile.getName();
            // �g���q.map��.evt�ɕύX����
            File eventFile = new File(mapFile.getParent() + File.separator
                    + filename.replaceAll(".map", ".evt"));
            // �C�x���g��ǂݍ���
            BufferedReader br = new BufferedReader(new FileReader(eventFile));
            // �C�x���g���i�[����ArrayList
            ArrayList eventList = new ArrayList();
            String line;
            while ((line = br.readLine()) != null) {
                // ��s�͓ǂݔ�΂�
                if (line.equals("")) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, ",");
                // �C�x���g�^�C�v
                String eventType = st.nextToken();
                if (eventType.equals("ENEMY")) { // �ړ��C�x���g
                    // ���W
                    int x = Integer.parseInt(st.nextToken());
                    int y = Integer.parseInt(st.nextToken());
                    // �}�b�v�`�b�v�ԍ�
                    int chipNo = Integer.parseInt(st.nextToken());
                    // �ړ���}�b�v�ԍ�
                    int destMapNo = Integer.parseInt(st.nextToken());
                    // �ړ�����W
                    int destX = Integer.parseInt(st.nextToken());
                    int destY = Integer.parseInt(st.nextToken());
                    // �C�x���g����
                    MoveEvent moveEvent = new MoveEvent(x, y, chipNo,
                            destMapNo, destX, destY);
                    // �ǉ�
                    eventList.add(moveEvent);
                } else if (eventType.equals("SPRING")) { // �L�����N�^�[�C�x���g
                    // ���W
                    int x = Integer.parseInt(st.nextToken());
                    int y = Integer.parseInt(st.nextToken());
                    // �L�����N�^�[�ԍ�
                    int charaNo = Integer.parseInt(st.nextToken());
                    // �����Ă������
                    int direction = Integer.parseInt(st.nextToken());
                    // �ړ��^�C�v
                    int moveType = Integer.parseInt(st.nextToken());
                    // ���b�Z�[�W
                    String message = st.nextToken();
                    // �C�x���g����
                    CharaEvent charaEvent = new CharaEvent(x, y, charaNo,
                            direction, moveType, message);
                    // �ǉ�
                    eventList.add(charaEvent);
                } else if (eventType.equals("NEEDLE")) {  // �󔠃C�x���g
                    // ���W
                    int x = Integer.parseInt(st.nextToken());
                    int y = Integer.parseInt(st.nextToken());
                    // �A�C�e����
                    String itemName = st.nextToken();
                    // �C�x���g����
                    TreasureEvent treasureEvent = new TreasureEvent(x, y, itemName);
                    // �ǉ�
                    eventList.add(treasureEvent);
                } else if (eventType.equals("ENEMY")) {  // �h�A�C�x���g
                    // ���W
                    int x = Integer.parseInt(st.nextToken());
                    int y = Integer.parseInt(st.nextToken());
                    // �C�x���g����
                    DoorEvent doorEvent = new DoorEvent(x, y);
                    // �ǉ�
                    eventList.add(doorEvent);
                }
            }
            // EventDialog�Ƀ��[�h�����C�x���g���Z�b�g
            eventDialog.setEvents(eventList);
            br.close();

            noSaveFlag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * �}�b�v���t�@�C���֏�������
     * 
     * @param mapFile �}�b�v�t�@�C��
     */
    public void saveMap(File mapFile) {
        try {
            // �}�b�v�̓o�C�i���t�@�C���Ƃ���
            // �}�b�v��1�}�X��1�o�C�g�ŕ\��
            FileOutputStream out = new FileOutputStream(mapFile);

            // �s���E�񐔂���������
            out.write(row);
            out.write(col>>8);
            out.write((byte)col);

            // �}�b�v����������
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    out.write(map[i][j]);
                }
            }

            out.close();

            System.out.println(mapFile.getName());
            // �C�x���g�t�@�C��
            String filename = mapFile.getName();
            // �g���q.map��.evt�ɕύX����
            File eventFile = new File(mapFile.getParent() + File.separator
                    + filename.replaceAll(".map", ".evt"));
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                    eventFile)));
            // �C�x���g���X�g���擾
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
     * �I�����Ă���}�b�v�`�b�v�Ń}�b�v��h��Ԃ�
     */
    public void fillMap() {
        // �p���b�g�őI������Ă���}�b�v�`�b�v�ԍ����擾
        int mapchipNo = paletteDialog.getSelectedMapchipNo();

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                map[i][j] = mapchipNo;
            }
        }

        repaint();
    }

    /**
     * �C�x���g��ǉ�����
     */
    public void addEvent() {
        // ���łɃC�x���g����������u���Ȃ�
        ArrayList eventList = eventDialog.getEvents();
        for (int i = 0; i < eventList.size(); i++) {
            Event evt = (Event) eventList.get(i);
            if (x == evt.x && y == evt.y) {
                JOptionPane.showMessageDialog(MainPanel.this, "���łɃC�x���g������܂�");
                return;
            }
        }

        eventDialog.setPos(x, y);
        eventDialog.setVisible(true);
    }

    /**
     * �C�x���g���폜����
     */
    public void removeEvent() {
        // �J�[�\���ʒu�̃C�x���g����������
        ArrayList eventList = eventDialog.getEvents();
        for (int i = 0; i < eventList.size(); i++) {
            Event evt = (Event) eventList.get(i);
            if (x == evt.x && y == evt.y) {
                eventList.remove(evt); // �C�x���g���폜
                repaint();
                return;
            }
        }
        JOptionPane.showMessageDialog(MainPanel.this, "�폜�ł���C�x���g�͂���܂���");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // map�ɕۑ�����Ă���}�b�v�`�b�v�ԍ������Ƃɉ摜��`�悷��
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                g.drawImage(mapchipImages[map[i][j]], j * CS, i * CS, null);
            }
        }

        // �C�x���g���擾���ĕ`��
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

        // �}�b�v�̑I���ʒu�ɃJ�[�\���\��
        g.setColor(Color.YELLOW);
        g.drawRect(x * CS, y * CS, CS, CS);
    }

    public void mouseClicked(MouseEvent e) {
        x = e.getX() / CS;
        y = e.getY() / CS;

        if (SwingUtilities.isLeftMouseButton(e)) { // ���N���b�N�̏ꍇ
            // �p���b�g����擾�����ԍ����Z�b�g
            if (x >= 0 && x < col && y >= 0 && y < row) {
                map[y][x] = paletteDialog.getSelectedMapchipNo();
                noSaveFlag = true;
            }
        } else if (SwingUtilities.isRightMouseButton(e)) { // �E�N���b�N�̏ꍇ
            // ���݈ʒu�̃}�b�v�`�b�v�ԍ����Z�b�g
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

        if (SwingUtilities.isLeftMouseButton(e)) { // ���N���b�N�̏ꍇ
            // �p���b�g����擾�����ԍ����Z�b�g
            if (x >= 0 && x < col && y >= 0 && y < row) {
                map[y][x] = paletteDialog.getSelectedMapchipNo();
                noSaveFlag = true;
            }
        } else if (SwingUtilities.isRightMouseButton(e)) { // �E�N���b�N�̏ꍇ
            // ���݈ʒu�̃}�b�v�`�b�v�ԍ����Z�b�g
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