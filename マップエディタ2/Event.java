/*
 * Created on 2005/11/12
 *
 */

/**
 * @author mori
 *
 */
public abstract class Event {
    // X���W
    protected int x;
    // Y���W
    protected int y;
    // �`�b�v�ԍ�
    protected int chipNo;
    // �Ԃ��邩
    protected boolean isHit;
    
    /**
     * �R���X�g���N�^
     * @param x X���W
     * @param y Y���W
     * @param chipNo �`�b�v�ԍ�
     * @param isHit �Ԃ��邩
     */
    public Event(int x, int y, int chipNo, boolean isHit) {
        this.x = x;
        this.y = y;
        this.chipNo = chipNo;
        this.isHit = isHit;
    }
    
    /**
     * �C�x���g�𕶎���ɕϊ��i�f�o�b�O�p�j
     */
    public String toString() {
        return x + "," + y + "," + chipNo + "," + isHit;
    }
}