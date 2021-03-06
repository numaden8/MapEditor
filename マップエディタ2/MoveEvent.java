/*
 * Created on 2005/12/03
 *
 */

/**
 * @author mori
 * 
 */
public class MoveEvent extends Event {
    // 移動先のマップ番号
	
    public int destMapNo;
    // 移動先のX座標
    public int destX;
    // 移動先のY座標
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
