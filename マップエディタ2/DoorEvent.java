/*
 * Created on 2005/12/03
 *
 */

/**
 * @author mori
 *
 */
public class DoorEvent extends Event {

    /**
     * @param x X���W
     * @param y Y���W
     */
    public DoorEvent(int x, int y) {
        // �Ƃт�̃`�b�v�ԍ���195�łԂ���
        super(x, y, 195, true);
    }
    
    /**
     * �C�x���g�𕶎���ɕϊ��i�f�o�b�O�p�j
     */
    public String toString() {
        return "ENEMY," + super.x + "," + super.y;
    }
}
