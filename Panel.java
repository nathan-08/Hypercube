import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.geom.*; // AffineTransform

class Point {
  public int x;
  public int y;
  public int z;
  public Point(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
public class Panel extends JPanel {
  private Tree tree = new Tree();
  private boolean mouseDown = false;
  private float scale_factor = 400.0f;
  private AffineTransform transform;
  private Point prevMousePosition = new Point(0, 0, 0);
  private double angle_1 = 0.0;
  private double angle_2 = 0.0;
  private double angle_3 = 0.0;
  private double angle_4 = 0.0;
  private boolean shift_key = false;
  private boolean ctrl_key = false;
  public void shift_pressed() {
    shift_key = true;
  }
  public void shift_released() {
    shift_key = false;
  }
  public void ctrl_pressed() {
    ctrl_key = true;
  }
  public void ctrl_released() {
    ctrl_key = false;
  }
  private int toInt(double x) {
    return (int)(float)Math.round(x);
  }
  public Dimension getPreferredSize() {
    return new Dimension(800, 800);
  }
  public void handleMousePressed() {}
  public void handleMouseReleased() {
    mouseDown = false;
  }
  public void handleMouseDragged(int x, int y) {
    int delta_x = Math.abs(x - prevMousePosition.x);
    int delta_y = Math.abs(y - prevMousePosition.y);
    if (!mouseDown) {
      mouseDown = true;
      delta_x = 0;
      delta_y = 0;
    }
    if (y >= prevMousePosition.y) {
      if (shift_key)
        angle_4 -= (Math.PI*(double)delta_y) / 400.0;
      angle_1 -= (Math.PI*(double)delta_y) / 400.0;
    }
    else if (y < prevMousePosition.y) {
      if (shift_key)
        angle_4 += (Math.PI*(double)delta_y) / 400.0;
      angle_1 += (Math.PI*(double)delta_y) / 400.0;
    }
    if (x > prevMousePosition.x) {
      if (shift_key)
        angle_3 -= (Math.PI*(double)delta_x) / 400.0;
      angle_2 -= (Math.PI*(double)delta_x) / 400.0;
    }
    else if (x < prevMousePosition.x) {
      if (shift_key)
        angle_3 += (Math.PI*(double)delta_x) / 400.0;
      angle_2 += (Math.PI*(double)delta_x) / 400.0;
    }
    prevMousePosition.x = x;
    prevMousePosition.y = y;
    repaint();
  }
  private float[] rotate_xw(float[]p, double a) {
    /* [ 1,   0,   0, 0, 0,] [ x ]
     * [ 0, cos,-sin, 0, 0,] [ y ]
     * [ 0, sin, cos, 0, 0,] [ z ]
     * [ 0,   0,   0, 1, 0,] [ w ]
     * [ 0,   0,   0, 0, 1,] [ s ]
    */
    float[] q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3];
    q[0] = x;
    q[1] = y*(float)Math.cos(a) - z*(float)Math.sin(a);
    q[2] = y*(float)Math.sin(a) + z*(float)Math.cos(a);
    q[3] = w;
    return q;
  }
  private float[] rotate_zw(float[]p, double a) {
    /* [   cos,   -sin,   0,    0, ] [ x ]
     * [   sin,    cos,   0,    0, ] [ y ]
     * [     0,      0,   1,    0, ] [ z ]
     * [     0,      0,   0,    1, ] [ w ]
    */
    float[] q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3];
    q[0] = x*(float)Math.cos(a) - y*(float)Math.sin(a);
    q[1] = x*(float)Math.sin(a) + y*(float)Math.cos(a);
    q[2] = z;
    q[3] = w;
    return q;
  }
  private float[] rotate_yz(float[]p, double a) {
    /* [   cos,      0,   0, -sin, ] [ x ]
     * [     0,      1,   0,    0, ] [ y ]
     * [     0,      0,   1,    0, ] [ z ]
     * [   sin,      0,   0,  cos, ] [ w ]
    */
    float[]q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3];
    q[0] = x * (float)Math.cos(a) - w * (float)Math.sin(a);
    q[1] = y;
    q[2] = z;
    q[3] = x * (float)Math.sin(a) + w*(float)Math.cos(a);
    return q;
  }
  private float[] rotate_xz(float[]p, double a) {
    /* [     1,      0,   0,    0, ] [ x ]
     * [     0,    cos,   0, -sin, ] [ y ]
     * [     0,      0,   1,    0, ] [ z ]
     * [     0,    sin,   0,  cos, ] [ w ]
    */
    float[] q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3];
    q[0] = x;
    q[1] = y*(float)Math.cos(a) - z*(float)Math.sin(a);
    q[2] = z;
    q[3] = y*(float)Math.sin(a) + w*(float)Math.cos(a);
    return q;
  }
  private float[] rotate_xy(float[]p, double a) {
    /* [     1,      0,   0,    0, ] [ x ]
     * [     0,      1,   0,    0, ] [ y ]
     * [     0,      0, cos, -sin, ] [ z ]
     * [     0,      0, sin,  cos, ] [ w ]
    */
    float[] q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3];
    q[0] = x;
    q[1] = y;
    q[2] = z*(float)Math.cos(a) - w*(float)Math.sin(a);
    q[3] = z*(float)Math.sin(a) + w*(float)Math.cos(a);
    return q;
  }
  private float[] rotate_ws(float[]p, double a) {
    /* [     1,  0,  0,   0,    0, ] [ x ]
     * [     0,  1,  0,   0,    0, ] [ y ]
     * [     0,  0,  1,   0,    0, ] [ z ]
     * [     0,  0,  0, cos, -sin, ] [ w ]
     * [     0,  0,  0, sin,  cos, ] [ s ]
    */
    float[] q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3]; float s = p[4];
    q[0] = x;
    q[1] = y;
    q[2] = z;
    q[3] = w*(float)Math.cos(a) - s*(float)Math.sin(a);
    q[4] = w*(float)Math.sin(a) + s*(float)Math.cos(a);
    return q;
  }
  private float[] rotate_tu(float[]p, double a) {
    /* [  1, 0,  0, 0,  0,   0,    0, ] [ x ]
     * [  0, 1,  0, 0,  0,   0,    0, ] [ y ]
     * [  0, 0,  1, 0,  0,   0,    0, ] [ z ]
     * [  0, 0,  0,cos,-sin,   0,    0, ] [ w ]
     * [  0, 0,  0,sin,cos,   0,    0, ] [ s ]
     * [  0, 0,  0, 0,  0, cos, -sin, ] [ t ]
     * [  0, 0,  0, 0,  0, sin,  cos, ] [ u ]
    */
    float[] q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3]; float s = p[4];
    float t = p[5]; float u = p[6];
    q[0] = x*(float)Math.cos(a) - y*(float)Math.sin(a);
    q[1] = x*(float)Math.sin(a) + y*(float)Math.cos(a);
    q[2] = z;
    q[3] = w;
    q[4] = s;
    q[5] = t*(float)Math.cos(a) - u*(float)Math.sin(a);
    q[6] = t*(float)Math.sin(a) + u*(float)Math.cos(a);
    return q;
  }
  private float[] rotate_yw(float[]p, double a) {
    /* [   cos,      0,-sin,    0, ] [ x ]
     * [     0,      1,   0,    0, ] [ y ]
     * [   sin,      0, cos,    0, ] [ z ]
     * [     0,      0,   0,    1, ] [ w ]
    */
    float[] q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3];
    q[0] = x*(float)Math.cos(a) - z*(float)Math.sin(a);
    q[1] = y;
    q[2] = x*(float)Math.sin(a) + z*(float)Math.cos(a);
    q[3] = w;
    return q;
  }
  private float[] rotate_xyzw(float[]p, double a) {
    /* [   cos,   -sin,   0,    0, ] [ x ]
     * [   sin,    cos,   0,    0, ] [ y ]
     * [     0,      0, cos, -sin, ] [ z ]
     * [     0,      0, sin,  cos, ] [ w ]
    */
    float[] q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3];
    q[0] = x*(float)Math.cos(a) - y*(float)Math.sin(a);
    q[1] = x*(float)Math.sin(a) + y*(float)Math.cos(x);
    q[2] = z*(float)Math.cos(a) - w*(float)Math.sin(a);
    q[3] = z*(float)Math.sin(a) + w*(float)Math.cos(a);
    return q;
  }
  private float[] project_6d(float[]p) {
    assert p.length >= 7 : "project_6d dimension mismatch";
    float[]q = p.clone();
    float x = p[0];float y = p[1];float z = p[2];float w = p[3];float s = p[4];
    float t = p[5]; float u = p[6];
    float denom = 2.4f - u;
    q[0] = x / denom;
    q[1] = y / denom;
    q[2] = z / denom;
    q[3] = w / denom;
    q[4] = s / denom;
    q[5] = t / denom;
    return q;
  }
  private float[] project_5d(float[]p) {
    assert p.length >= 6 : "project_5d dimension mismatch";
    float[]q = p.clone();
    float x = p[0];float y = p[1];float z = p[2];float w = p[3];float s = p[4];
    float t = p[5];
    float denom = 2.2f - t;
    q[0] = x / denom;
    q[1] = y / denom;
    q[2] = z / denom;
    q[3] = w / denom;
    q[4] = s / denom;
    return q;
  }
  private float[] project_4d(float[]p) {
    assert p.length >= 5 : "project_4d dimension mismatch";
    float[]q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3]; float s = p[4];
    float denom = 2.2f - s;
    q[0] = x / denom;
    q[1] = y / denom;
    q[2] = z / denom;
    q[3] = w / denom;
    q[4] = 0.0f;
    return q;
  }
  private float[] project_3d(float[]p) {
    // stereographic projection
    /* [1/(lw-w),       0,       0,    0, ] [ x ]
     * [       0,1/(lw-w),       0,    0, ] [ y ]
     * [       0,       0,1/(lw-w),    0, ] [ z ]
    */
    float[] q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3];
    float denom = 2.4f - w;
    q[0] = x / denom;
    q[1] = y / denom;
    q[2] = z / denom;
    q[3] = 0.0f;
    return q;
  }
  private float[] project_2d(float[]p) {
    float[] q = p.clone();
    float x = p[0]; float y = p[1]; float z = p[2]; float w = p[3];
    float denom = 2.8f - z;
    q[0] = x / denom;
    q[1] = y / denom;
    q[2] = 0.0f;
    q[3] = 0.0f;
    return q;
  }
  private int[] scale(float[]p) {
    int[] q = new int[p.length];
    q[0] = toInt(p[0] * scale_factor);
    q[1] = toInt(p[1] * scale_factor);
    //q[2] = toInt(p[2] * scale_factor);
    //q[3] = toInt(p[3] * scale_factor);
    //q[4] = toInt(p[4] * scale_factor);
    //q[5] = toInt(p[5] * scale_factor);
    return q;
  }
  private float[] to_float(int[]p) {
    float[] q = new float[p.length];
    for (int i = 0; i < p.length; ++i)
      q[i] = (float)p[i];
    return q;
  }
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    int w = getWidth();
    int h = getHeight();

    transform = new AffineTransform();
    transform.translate(getWidth()/2, getHeight()/2);
    transform.scale(1,-1);
    g2d.setTransform(transform);

    g2d.setColor(Color.black);
    g2d.fillRect(-w/2,-h/2, w, h);
    g2d.setColor(Color.blue);

    g2d.setStroke(new BasicStroke(
          3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

    tree.for_each_edge((p1, p2) -> {
      if (p1[3] == -1 && p2[3] == -1) {
        g2d.setColor(Color.blue);
      }
      else {
        g2d.setColor(new Color(0x22, 0x88, 0xcc));
      }
      if (p1[3] != p2[3]) {
        g2d.setColor(new Color(0x44, 0x33, 0xaa));
      }
      float[]q1 = to_float(p1);
      float[]q2 = to_float(p2);

      q1 = rotate_xw(q1, angle_1); // up-down
      q2 = rotate_xw(q2, angle_1);

      q1 = rotate_xy(q1, angle_4); // shift-mod
      q2 = rotate_xy(q2, angle_4);

      q1 = rotate_yw(q1, angle_2); // left-right
      q2 = rotate_yw(q2, angle_2);
      q1 = rotate_yz(q1, angle_3);
      q2 = rotate_yz(q2, angle_3);

      //q1 = rotate_tu(q1, angle_3); // shift-mod
      //q2 = rotate_tu(q2, angle_3);

      //q1 = project_6d(q1);
      //q2 = project_6d(q2);

      //q1 = project_5d(q1);
      //q2 = project_5d(q2);

      //q1 = project_4d(q1);
      //q2 = project_4d(q2);

      q1 = project_3d(q1);
      q2 = project_3d(q2);

      q1 = project_2d(q1);
      q2 = project_2d(q2);

      p1 = scale(q1);
      p2 = scale(q2);

      g2d.drawLine(p1[0], p1[1], p2[0], p2[1]);
    });
  }
}

