import java.util.Arrays;
import java.util.ArrayList;

public class Inheritance_test {
    public static void main (String[] args) throws Exception {
        ASub aSub = new ASub (new BSub (1, 5), new BSub (2, 3));
        System.out.println (aSub.bList.size ());
    }
}

class ASuper<T extends BSuper> {
    T bInstance1;
    T bInstance2;

    ArrayList<T> bList;

    ASuper (T bSuper1, T bSuper2) {
        bInstance1 = bSuper1;
        bInstance2 = bSuper2;

        bList = new ArrayList<T> (Arrays.asList (bInstance1, bInstance2));
    }
}

class ASub extends ASuper<BSub> {
    ASub (BSub bSub1, BSub bSub2) {
        super (bSub1, bSub2);
    }
}

class BSuper {
    int elementSuper;

    BSuper (int elementSuper) {
        this.elementSuper = elementSuper;
    }
}

class BSub extends BSuper {
    int elementSub;

    BSub (int elementSuper, int elementSub) {
        super (elementSuper);
        this.elementSub = elementSub;
    }

}