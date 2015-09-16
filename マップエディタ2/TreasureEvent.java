/*
 * Created on 2005/11/19
 *
 */

/**
 * @author mori
 *
 */
public class TreasureEvent extends Event {
    // �󔠂ɓ����Ă���A�C�e����
    private String itemName;

    /**
     * @param x X���W
     * @param y Y���W
     * @param itemName ��ɓ���A�C�e����
     */
    public TreasureEvent(int x, int y, String itemName) {
        // �󔠂̃`�b�v�ԍ���194�łԂ���Ȃ�
        super(x, y, 194, false);
        this.itemName = itemName;
    }

    /**
     * �A�C�e������Ԃ�
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * �C�x���g�𕶎���ɕϊ��i�f�o�b�O�p�j
     */
    public String toString() {
    	return "NEEDLE," + super.x + "," + super.y;
    }
}
