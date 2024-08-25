import java.util.Arrays;
import java.util.ArrayList;

public class CUBE_virtual {
    public static void main (String[] args) throws Exception {
        Cube cube = new Cube (6);
        cube.doMove ("2r");
        cube.show ();
    }
}

class Face { // 面．
    protected int nPart; // パーツの数．

    protected int[] permutationCw; // 面を時計回りに90°回した時の置換．
    protected int[] permutationCcw; // 面を反時計回りに90°回した時の置換．
    protected int[] permutationTwo; // 面を180°回した時の置換．

    protected String faceName; // 面の名前．

    Face (int nPart, String faceName) { // パーツの数と面の名前のみ登録．
        this.nPart = nPart;
        this.faceName = faceName;

        // permutationの定義．
        permutationCw = General.stepArray (nPart);
        permutationCcw = General.stepArray (nPart);
        permutationTwo = General.stepArray (nPart);
    }

    void defineTurn (int[][] permutation) { // 回転の定義．

        for (int i = 0; i < permutation.length; i++) {
            for (int j = 0; j < permutation[i].length; j++) {
                permutationCw[permutation[i][(j + 1) % permutation[i].length]] = permutation[i][j];
            }
        }

        permutationCcw = General.inverse (permutationCw);
        permutationTwo = General.permutate (permutationCw, permutationCw);
    }
}

class FaceOrient extends Face {
    protected int nOrient; // パーツがとりうる向きの数．

    protected int[] orientationCw; // 面を時計回りに90°回した時の向きの変化．
    protected int[] orientationCcw; // 面を反時計回りに90°回した時の向きの変化．
    protected int[] orientationTwo; // 面を180°回した時の向きの変化．

    FaceOrient (int nPart, int nOrient, String faceName) { // パーツの数と向きの数，面の名前のみ登録．
        super (nPart, faceName);

        this.nOrient = nOrient;

        // orientationの定義．
        orientationCw = new int[nPart];
        orientationCcw = new int[nPart];
        orientationTwo = new int[nPart];        
    }

    void defineTurnOrient (int[][] permutation, int[][] orientation) {
        defineTurn (permutation);
        
        for (int i = 0; i < permutation.length; i++) {
            for (int j = 0; j < permutation[i].length; j++) {
                orientationCw[permutation[i][j]] = orientation[i][j];
            }
        }

        orientationCcw = General.scalarMod (General.permutate (orientationCw, permutationCw), -1, nOrient);
        orientationTwo = General.sumMod (orientationCw, General.permutate (orientationCw, permutationCcw), nOrient);
    }
}

abstract class Part<F extends Face> { // パーツ．
    protected int nPart; // パーツの数．
    protected int[] position; // パーツの位置．
    
    // 面の定義．

    // 外層．
    protected F u, d, f, b, r, l;
    // 中層の外側．
    protected F uOuter, dOuter, fOuter, bOuter, rOuter, lOuter;
    // 中層の内側．
    protected F uInner, dInner, fInner, bInner, rInner, lInner;
    // 中層．
    protected F e, s, m;

    protected ArrayList<F> faceContent; // 各面の内容を格納．

    Part (int nPart) {
        this.nPart = nPart;
        position = General.stepArray (nPart);
    }

    void makeFaceContent () {
        faceContent = new ArrayList<F> (Arrays.asList (
            u, d, f, b, r, l,
            uOuter, dOuter, fOuter, bOuter, rOuter, lOuter,
            uInner, dInner, fInner, bInner, rInner, lInner,
            e, s, m
        ));
    }

    abstract void turnFace (String faceName, int cw);

    void show () { // パーツを表示する．
        System.out.println ("位置: " + Arrays.toString (position));
    }
}

abstract class PartNotOrient extends Part<Face> { // 向きが変わり得ないパーツ．
    PartNotOrient (int nPart) {
        super (nPart);

        // 回転の名前のみ定義．
        u = new Face (nPart, "U");
        d = new Face (nPart, "D");
        f = new Face (nPart, "F");
        b = new Face (nPart, "B");
        r = new Face (nPart, "R");
        l = new Face (nPart, "L");
        
        uOuter = new Face (nPart, "uOuter");
        dOuter = new Face (nPart, "dOuter");
        fOuter = new Face (nPart, "fOuter");
        bOuter = new Face (nPart, "bOuter");
        rOuter = new Face (nPart, "rOuter");
        lOuter = new Face (nPart, "lOuter");
        
        uInner = new Face (nPart, "uInner");
        dInner = new Face (nPart, "dInner");
        fInner = new Face (nPart, "fInner");
        bInner = new Face (nPart, "bInner");
        rInner = new Face (nPart, "rInner");
        lInner = new Face (nPart, "lInner");
        
        e = new Face (nPart, "E");
        s = new Face (nPart, "S");
        m = new Face (nPart, "M");

        makeFaceContent ();
    }

    @Override
    void turnFace (String faceName, int cw) { // 面を回す．cw=1が時計回りに90°，-1が反時計回りに90°，2が180°回転．
        for (int i = 0; i < faceContent.size (); i++) { // 面を探す．
            if (faceName.equals (faceContent.get (i).faceName)) {
                int[] movePermutation; // 動きの置換．
                if (cw == 1) {
                    movePermutation = faceContent.get (i).permutationCw;
                }
                else if (cw == -1) {
                    movePermutation = faceContent.get (i).permutationCcw;
                }
                else {
                    movePermutation = faceContent.get (i).permutationTwo;
                }
                position = General.permutate (position, movePermutation);

                break;
            }
        }
    }
}

abstract class PartOrient extends Part<FaceOrient> { // 向きが変わり得るパーツ．
    protected int nOrient; // パーツがとりうる向きの数．
    protected int[] orientation; // パーツの向き．

    PartOrient (int nPart, int nOrient) {
        super (nPart);

        // 回転の名前のみ定義．
        u = new FaceOrient (nPart, nOrient, "U");
        d = new FaceOrient (nPart, nOrient, "D");
        f = new FaceOrient (nPart, nOrient, "F");
        b = new FaceOrient (nPart, nOrient, "B");
        r = new FaceOrient (nPart, nOrient, "R");
        l = new FaceOrient (nPart, nOrient, "L");
        
        uOuter = new FaceOrient (nPart, nOrient, "uOuter");
        dOuter = new FaceOrient (nPart, nOrient, "dOuter");
        fOuter = new FaceOrient (nPart, nOrient, "fOuter");
        bOuter = new FaceOrient (nPart, nOrient, "bOuter");
        rOuter = new FaceOrient (nPart, nOrient, "rOuter");
        lOuter = new FaceOrient (nPart, nOrient, "lOuter");
        
        uInner = new FaceOrient (nPart, nOrient, "uInner");
        dInner = new FaceOrient (nPart, nOrient, "dInner");
        fInner = new FaceOrient (nPart, nOrient, "fInner");
        bInner = new FaceOrient (nPart, nOrient, "bInner");
        rInner = new FaceOrient (nPart, nOrient, "rInner");
        lInner = new FaceOrient (nPart, nOrient, "lInner");
        
        e = new FaceOrient (nPart, nOrient, "E");
        s = new FaceOrient (nPart, nOrient, "S");
        m = new FaceOrient (nPart, nOrient, "M");

        makeFaceContent ();

        this.nOrient = nOrient;
        orientation = new int[nPart];
    }

    @Override
    void turnFace (String faceName, int cw) { // 面を回す．cw=1が時計回りに90°，-1が反時計回りに90°，2が180°回転．        
        for (int i = 0; i < faceContent.size (); i++) { // 面を探す．
            if (faceName.equals (faceContent.get (i).faceName)) {
                int[] movePermutation; // 動きの置換．
                int[] moveOrientation; // 動きによる向きの変化．

                if (cw == 1) { // 時計回りに90°．
                    movePermutation = faceContent.get (i).permutationCw;
                    moveOrientation = faceContent.get (i).orientationCw;
                }
                else if (cw == -1) { // 反時計回りに180°．
                    movePermutation = faceContent.get (i).permutationCcw;
                    moveOrientation = faceContent.get (i).orientationCcw;
                }
                else {
                    movePermutation = faceContent.get (i).permutationTwo;
                    moveOrientation = faceContent.get (i).orientationTwo;
                }

                position = General.permutate (position, movePermutation);
                orientation = General.permutate (General.sumMod (orientation, moveOrientation, nOrient), movePermutation);

                break;
            }
        }
    }

    @Override
    void show () { // パーツを表示する．
        super.show ();
        System.out.println ("向き: " + Arrays.toString (orientation));
    }
}

class Corner extends PartOrient { // コーナー.

    Corner () {
        super (8, 3);

        // 回転面とそれに伴う動きの定義．
        u.defineTurnOrient (new int[][] {{0, 1, 2, 3}}, new int[1][4]);
        d.defineTurnOrient (new int[][] {{4, 5, 6, 7}}, new int[1][4]);
        f.defineTurnOrient (new int[][] {{3, 2, 5, 4}}, new int[][] {{1, 2, 1, 2}});
        b.defineTurnOrient (new int[][] {{1, 0, 7, 6}}, new int[][] {{1, 2, 1, 2}});
        r.defineTurnOrient (new int[][] {{2, 1, 6, 5}}, new int[][] {{1, 2, 1, 2}});
        l.defineTurnOrient (new int[][] {{0, 3, 4, 7}}, new int[][] {{1, 2, 1, 2}});
    }
}

class Center extends PartNotOrient { // センターキューブ．
    Center () {
        super (6);

        // 回転面とそれに伴う動きの定義．
        e.defineTurn (new int[][] {{1, 2, 3, 4}});
        s.defineTurn (new int[][] {{0, 2, 5, 4}});
        m.defineTurn (new int[][] {{0, 1, 5, 3}});
    }
}

class MiddleEdge extends PartOrient { // ミドルエッジ．
    MiddleEdge () {
        super (12, 2);

        // 回転面とそれに伴う動きの定義．
        u.defineTurnOrient (new int[][] {{0, 1, 2, 3}}, new int[1][4]);
        d.defineTurnOrient (new int[][] {{8, 9, 10, 11}}, new int[1][4]);
        f.defineTurnOrient (new int[][] {{2, 4, 8, 5}}, new int[][] {{1, 1, 1, 1}});
        b.defineTurnOrient (new int[][] {{0, 6, 10, 7}}, new int[][] {{1, 1, 1, 1}});
        r.defineTurnOrient (new int[][] {{1, 7, 9, 4}}, new int[1][4]);
        l.defineTurnOrient (new int[][] {{3, 5, 11, 6}}, new int[1][4]);

        e.defineTurnOrient (new int[][] {{5, 4, 7, 6}}, new int[][] {{1, 1, 1, 1}});
        s.defineTurnOrient (new int[][] {{3, 1, 9, 11}}, new int[][] {{1, 1, 1, 1}});
        m.defineTurnOrient (new int[][] {{0, 2, 8, 10}}, new int[][] {{1, 1, 1, 1}});
    }
}

class WingEdge extends PartNotOrient { // ウィングエッジ．
    WingEdge () {
        super (24);

        // 回転面とそれに伴う動きの定義．
        u.defineTurn (new int[][] {{0, 1, 2, 3}, {12, 8, 4, 16}});
        d.defineTurn (new int[][] {{20, 21, 22, 23}, {6, 10, 14, 18}});
        f.defineTurn (new int[][] {{4, 5, 6, 7}, {2, 11, 20, 17}});
        b.defineTurn (new int[][] {{12, 13, 14, 15}, {0, 19, 22, 9}});
        r.defineTurn (new int[][] {{8, 9, 10, 11}, {1, 15, 21, 5}});
        l.defineTurn (new int[][] {{16, 17, 18, 19}, {3, 7, 23, 13}});

        uOuter.defineTurn (new int[][] {{17, 13, 9, 5}});
        dOuter.defineTurn (new int[][] {{7, 11, 15, 19}});
        fOuter.defineTurn (new int[][] {{3, 8, 21, 18}});
        bOuter.defineTurn (new int[][] {{1, 16, 23, 10}});
        rOuter.defineTurn (new int[][] {{2, 12, 22, 6}});
        lOuter.defineTurn (new int[][] {{0, 4, 20, 14}});
    }
}

class XCenter extends PartNotOrient { // Xセンター．
    XCenter () {
        super (24);

        // 回転面とそれに伴う動きの定義．
        u.defineTurn (new int[][] {{0, 1, 2, 3}});
        d.defineTurn (new int[][] {{20, 21, 22, 23}});
        f.defineTurn (new int[][] {{4, 5, 6, 7}});
        b.defineTurn (new int[][] {{12, 13, 14, 15}});
        r.defineTurn (new int[][] {{8, 9, 10, 11}});
        l.defineTurn (new int[][] {{16, 17, 18, 19}});

        uOuter.defineTurn (new int[][] {{16, 12, 8, 4}, {17, 13, 9, 5}});
        dOuter.defineTurn (new int[][] {{6, 10, 14, 18}, {7, 11, 15, 19}});
        fOuter.defineTurn (new int[][] {{2, 11, 20, 17}, {3, 8, 21, 18}});
        bOuter.defineTurn (new int[][] {{0, 19, 22, 9}, {1, 16, 23, 10}});
        rOuter.defineTurn (new int[][] {{1, 15, 21, 5}, {2, 12, 22, 6}});
        lOuter.defineTurn (new int[][] {{0, 4, 20, 14}, {3, 7, 23, 13}});
    }
}

class TCenter extends PartNotOrient { // Tセンター．
    TCenter () {
        super (24);

        // 回転面とそれに伴う動きの定義．
        u.defineTurn (new int[][] {{0, 1, 2, 3}});
        d.defineTurn (new int[][] {{20, 21, 22, 23}});
        f.defineTurn (new int[][] {{4, 5, 6, 7}});
        b.defineTurn (new int[][] {{12, 13, 14, 15}});
        r.defineTurn (new int[][] {{8, 9, 10, 11}});
        l.defineTurn (new int[][] {{16, 17, 18, 19}});
        
        uOuter.defineTurn (new int[][] {{16, 12, 8, 4}});
        dOuter.defineTurn (new int[][] {{6, 10, 14, 18}});
        fOuter.defineTurn (new int[][] {{2, 11, 20, 17}});
        bOuter.defineTurn (new int[][] {{0, 19, 22, 9}});
        rOuter.defineTurn (new int[][] {{1, 15, 21, 5}});
        lOuter.defineTurn (new int[][] {{3, 7, 23, 13}});

        e.defineTurn (new int[][] {{5, 9, 13, 17}, {7, 11, 15, 19}});
        s.defineTurn (new int[][] {{1, 10, 23, 16}, {3, 8, 21, 18}});
        m.defineTurn (new int[][] {{0, 4, 20, 14}, {2, 6, 22, 12}});
    }
}

class RightObliqueCenter extends PartNotOrient { // 右斜めセンター．
    RightObliqueCenter () {
        super (24);

        // 回転面とそれに伴う動きの定義．
        u.defineTurn (new int[][] {{0, 1, 2, 3}});
        d.defineTurn (new int[][] {{20, 21, 22, 23}});
        f.defineTurn (new int[][] {{4, 5, 6, 7}});
        b.defineTurn (new int[][] {{12, 13, 14, 15}});
        r.defineTurn (new int[][] {{8, 9, 10, 11}});
        l.defineTurn (new int[][] {{16, 17, 18, 19}});

        uOuter.defineTurn (new int[][] {{16, 12, 8, 4}});
        dOuter.defineTurn (new int[][] {{6, 10, 14, 18}});
        fOuter.defineTurn (new int[][] {{2, 11, 20, 17}});
        bOuter.defineTurn (new int[][] {{0, 19, 22, 9}});
        rOuter.defineTurn (new int[][] {{1, 15, 21, 5}});
        lOuter.defineTurn (new int[][] {{3, 7, 23, 13}});

        uInner.defineTurn (new int[][] {{17, 13, 9, 5}});
        dInner.defineTurn (new int[][] {{7, 11, 15, 19}});
        fInner.defineTurn (new int[][] {{3, 8, 21, 18}});
        bInner.defineTurn (new int[][] {{1, 16, 23, 10}});
        rInner.defineTurn (new int[][] {{2, 12, 22, 6}});
        lInner.defineTurn (new int[][] {{0, 4, 20, 14}});
    }
}

class LeftObliqueCenter extends PartNotOrient { // 左斜めセンター．
    LeftObliqueCenter () {
        super (24);

        // 回転面とそれに伴う動きの定義．
        u.defineTurn (new int[][] {{0, 1, 2, 3}});
        d.defineTurn (new int[][] {{20, 21, 22, 23}});
        f.defineTurn (new int[][] {{4, 5, 6, 7}});
        b.defineTurn (new int[][] {{12, 13, 14, 15}});
        r.defineTurn (new int[][] {{8, 9, 10, 11}});
        l.defineTurn (new int[][] {{16, 17, 18, 19}});

        uOuter.defineTurn (new int[][] {{16, 12, 8, 4}});
        dOuter.defineTurn (new int[][] {{6, 10, 14, 18}});
        fOuter.defineTurn (new int[][] {{2, 11, 20, 17}});
        bOuter.defineTurn (new int[][] {{0, 19, 22, 9}});
        rOuter.defineTurn (new int[][] {{1, 15, 21, 5}});
        lOuter.defineTurn (new int[][] {{3, 7, 23, 13}});

        uInner.defineTurn (new int[][] {{19, 15, 11, 7}});
        dInner.defineTurn (new int[][] {{5, 9, 13, 17}});
        fInner.defineTurn (new int[][] {{1, 10, 23, 16}});
        bInner.defineTurn (new int[][] {{3, 18, 21, 8}});
        rInner.defineTurn (new int[][] {{0, 14, 20, 4}});
        lInner.defineTurn (new int[][] {{2, 6, 22, 12}});
    }
}

class Cube {
    private int n;

    private Corner[] corner;
    private Center[] center;
    private MiddleEdge[] middleEdge;
    private WingEdge[] wingEdge;
    private XCenter[] xCenter;
    private TCenter[] tCenter;
    private RightObliqueCenter[][] rightObliqueCenter;
    private LeftObliqueCenter[][] leftObliqueCenter;

    Cube (int n) {
        this.n = n; // 分割数．2以上．
        
        corner = new Corner[] {new Corner ()}; // コーナー．
        
        if ((n % 2) == 0) { // 偶数分割の場合．
            // センター，ミドルエッジ，+センターはない．
            center = new Center[] {};
            middleEdge = new MiddleEdge[] {};
            tCenter = new TCenter[] {};
        }
        else { // 奇数分割の場合．
            center = new Center[] {new Center ()};
            middleEdge = new MiddleEdge[] {new MiddleEdge ()};
            if (n >= 5) { // 5以上の奇数分割の場合，+センターがある．
                tCenter = new TCenter[(n - 3) / 2];
                for (int i = 0; i < tCenter.length; i++) {
                    tCenter[i] = new TCenter ();
                }
            }
            else {
                tCenter = new TCenter[] {};
            }
        }

        if (n >= 4) { // 4以上の多分割の場合，ウィングエッジとXセンターがある．
            wingEdge = new WingEdge[(n - 2) / 2];
            xCenter = new XCenter[wingEdge.length];

            for (int i = 0; i < wingEdge.length; i++) {
                wingEdge[i] = new WingEdge ();
                xCenter[i] = new XCenter ();
            }
        }
        else {
            wingEdge = new WingEdge[] {};
            xCenter = new XCenter[] {};
        }

        if (n >= 6) { // 6以上の多分割の場合，斜めセンターがある．
            rightObliqueCenter = new RightObliqueCenter[(n - 4) / 2][];
            leftObliqueCenter = new LeftObliqueCenter[rightObliqueCenter.length][];
            for (int i = 0; i < rightObliqueCenter.length; i++) {
                rightObliqueCenter[i] = new RightObliqueCenter[(n / 2) - 2 - i];
                leftObliqueCenter[i] = new LeftObliqueCenter[rightObliqueCenter[i].length];
                for (int j = 0; j < rightObliqueCenter[i].length; j++) {
                    rightObliqueCenter[i][j] = new RightObliqueCenter ();
                    leftObliqueCenter[i][j] = new LeftObliqueCenter ();
                }
            }
        }
        else {
            rightObliqueCenter = new RightObliqueCenter[][] {};
            leftObliqueCenter = new LeftObliqueCenter[][] {};
        }
    }

    void doMove (String moveName) { // キューブを回す．
        if (Character.isDigit (moveName.charAt (0))) { // 最初の文字が数字かどうか．
            // 最初の数字が何文字か調べる．
            int numberFinish = moveName.length (); // 何文字目まで数字か．

            for (int i = 0; i < moveName.length (); i++) {
                if (Character.isDigit (moveName.charAt (i))) { // もし数字だったら．
                    continue;
                }
                else {
                    numberFinish = i;
                    break;
                }
            }

            int layer = Integer.parseInt (moveName.substring (0, numberFinish)); // 何層目か．

            if ((layer <= n / 2) && (layer >= 2)) { // 数字が適切な範囲ならば実行．
                String faceName; // 面の名前．
                int cw; // 回転方向．

                if (moveName.charAt (moveName.length () - 1) == '\'') { // 反時計に90°回転のときはプライム記号．
                    faceName = moveName.substring (numberFinish, moveName.length () - 1);
                    cw = -1;
                }
                else if (moveName.charAt (moveName.length () - 1) == '2') { // 180°回転のときは2．
                    faceName = moveName.substring (numberFinish, moveName.length () - 1);
                    cw = 2;
                }
                else { // 時計回りに90°回転はそのまま．
                    faceName = moveName.substring (numberFinish, moveName.length ());
                    cw = 1;
                }

                // (layer)層目を回す．
                wingEdge[layer - 2].turnFace (faceName + "Outer", cw);
                xCenter[layer - 2].turnFace (faceName + "Outer", cw);
                if (n % 2 == 1) { // 奇数分割ならば．
                    tCenter[layer - 2].turnFace (faceName + "Outer", cw);
                }

                if (layer - 2 < rightObliqueCenter.length) {
                    for (int i = 0; i < rightObliqueCenter[layer - 2].length; i++) { // (layer)層目が外側である斜めセンター．
                        rightObliqueCenter[layer - 2][i].turnFace (faceName + "Outer", cw);
                        leftObliqueCenter[layer - 2][i].turnFace (faceName + "Outer", cw);
                    }
                }
                for (int i = 0; i < layer - 2; i++) { // (layer)層目が内側である斜めセンター．
                    rightObliqueCenter[i][layer - 3 - i].turnFace (faceName + "Inner", cw);
                    leftObliqueCenter[i][layer - 3 - i].turnFace (faceName + "Inner", cw);
                }
            }
        }
        else { // 最初の文字が数字じゃなかったら外層あるいは中層(E, S, M)．
            String faceName; // 面の名前．
            int cw; // 回転方向．

            if (moveName.charAt (moveName.length () - 1) == '\'') { // 反時計に90°回転のときはプライム記号．
                faceName = moveName.substring (0, moveName.length () - 1);
                cw = -1;
            }
            else if (moveName.charAt (moveName.length () - 1) == '2') { // 180°回転のときは2．
                faceName = moveName.substring (0, moveName.length () - 1);
                cw = 2;
            }
            else { // 時計回りに90°回転はそのまま．
                faceName = moveName;
                cw = 1;
            }

            for (int i = 0; i < corner.length; i++) {
                corner[i].turnFace (faceName, cw);
            }
            for (int i = 0; i < center.length; i++) {
                center[i].turnFace (faceName, cw);
            }
            for (int i = 0; i < middleEdge.length; i++) {
                middleEdge[i].turnFace (faceName, cw);
            }
            for (int i = 0; i < wingEdge.length; i++) {
                wingEdge[i].turnFace (faceName, cw);
            }
            for (int i = 0; i < xCenter.length; i++) {
                xCenter[i].turnFace (faceName, cw);
            }
            for (int i = 0; i < tCenter.length; i++) {
                tCenter[i].turnFace (faceName, cw);
            }
            for (int i = 0; i < rightObliqueCenter.length; i++) {
                for (int j = 0; j < rightObliqueCenter[i].length; j++) {
                    rightObliqueCenter[i][j].turnFace (faceName, cw);
                    leftObliqueCenter[i][j].turnFace (faceName, cw);
                }
            }
        }
    }

    void show () { // キューブを表示する．
        System.out.println (n + "x" + n + "x" + n + "キューブの状態");
        System.out.println ();

        for (int i = 0; i < corner.length; i++) {
            System.out.println ("コーナー");
            corner[i].show ();
            System.out.println ();
        }

        for (int i = 0; i < center.length; i++) {
            System.out.println ("センター");
            center[i].show ();
            System.out.println ();
        }

        for (int i = 0; i < middleEdge.length; i++) {
            System.out.println ("ミドルエッジ");
            middleEdge[i].show ();
            System.out.println ();
        }

        for (int i = 0; i < wingEdge.length; i++) {
            System.out.println ("ウィングエッジ (" + (2 + i) + ")");
            wingEdge[i].show ();
            System.out.println ();
        }

        for (int i = 0; i < xCenter.length; i++) {
            System.out.println ("Xセンター (" + (2 + i) + ")");
            xCenter[i].show ();
            System.out.println ();
        }

        for (int i = 0; i < tCenter.length; i++) {
            System.out.println ("Tセンター (" + (2 + i) + ")");
            tCenter[i].show ();
            System.out.println ();
        }        
        
        for (int i = 0; i < rightObliqueCenter.length; i++) {
            for (int j = 0; j < rightObliqueCenter[i].length; j++) {
                System.out.println ("右斜めセンター (" + (2 + i) + ", " + (3 + i + j) + ")");
                rightObliqueCenter[i][j].show ();
                System.out.println ();
            }
        }

        for (int i = 0; i < leftObliqueCenter.length; i++) {
            for (int j = 0; j < leftObliqueCenter[i].length; j++) {
                System.out.println ("左斜めセンター (" + (2 + i) + ", " + (3 + i + j) + ")");
                leftObliqueCenter[i][j].show ();
                System.out.println ();
            }
        }
    }
}


class General {
    static int[] stepArray (int n) { // {1, 2, ... , n}という配列を作る．
        int[] stepArray = new int[n];
        for (int i = 0; i < n; i++) {
            stepArray[i] = i;
        }
        return stepArray;
    }

    static int[] permutate (int[] a, int[] b) { // 置換の積abを求める．
        int[] ab = new int[a.length];

        for (int i = 0; i < a.length; i++) {
            ab[i] = a[b[i]];
        }

        return ab;
    }

    static int[] inverse (int[] a) { // aの逆置換を求める．
        int[] aInverse = new int[a.length];
        
        for (int i = 0; i < a.length; i++) {
            aInverse[a[i]] = i;
        }

        return aInverse;
    }

    static int mod (int n, int m) { // 余りを求める．
        int mod = n % m;
        if (mod < 0) {
            mod += m;
        }

        return mod;
    }

    static int[] sumMod (int[] a, int[] b, int m) { // a+bをmod mで整える．
        int[] aPlusB = new int[a.length];
        
        for (int i = 0; i < a.length; i++) {
            aPlusB[i] = mod (a[i] + b[i], m);
        }

        return aPlusB;
    }

    static int[] scalarMod (int[] a, int k, int m) { // 配列aをk倍してmod mで整理する．
        int[] ka = new int[a.length]; // スカラー倍された配列．
        
        for (int i = 0; i < a.length; i++) {
            ka[i] = mod (a[i] * k, m);
        }

        return ka;
    }

}