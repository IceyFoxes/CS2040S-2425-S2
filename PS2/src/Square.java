public class Square extends Rectangle {
    public Square(double length) {
        super(length, length);
    }
    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        super.setWidth(height);
    }
    @Override
    public void setWidth(double width) {
        super.setHeight(width);
        super.setWidth(width);
    }
}