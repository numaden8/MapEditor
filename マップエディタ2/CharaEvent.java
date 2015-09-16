/*
 * Created on 2006/12/02
 */

public class CharaEvent extends Event {
    // 向いている方向
    public int direction;
    // 移動タイプ
    public int moveType;
    // メッセージ
    public String message;

    public CharaEvent(int x, int y, int charaNo, int direction, int moveType,
            String message) {
        super(x, y, charaNo, true);
        this.direction = direction;
        this.moveType = moveType;
        this.message = message;
    }

    public String toString() {
//        return "CHARA," + super.x + "," + super.y + "," + super.chipNo + "," + direction
//                + "," + moveType + "," + message;
    	return "SPRING," + super.x + "," + super.y;
    }
}
