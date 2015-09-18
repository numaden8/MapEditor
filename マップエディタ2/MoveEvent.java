/*
 * Created on 2005/12/03
 *
 */

/**
 * @author mori
 * 
 */
public class MoveEvent extends Event {
    // �ړ���̃}�b�v�ԍ�
	
    public int destMapNo;
    // �ړ����X���W
    public int destX;
    // �ړ����Y���W
    public int destY;

    public MoveEvent(int x, int y, int chipNo, int destMapNo, int destX,
            int destY) {
        super(x, y, chipNo, false);
        this.destMapNo = destMapNo;
        this.destX = destX;
        this.destY = destY;
    }

    public String toString() {
        return "ENEMY," + super.x + "," + super.y;
    }
}
