public class Inheritance_test2 {
    public static void main (String[] args) throws Exception {
        TestSub1 sub1 = new TestSub1 (1, 2);
        TestSub2 sub2 = new TestSub2 (2, 3);

        TestSuper[] subs = new TestSuper[] {sub1, sub2};
    }
}

class TestSuper {
    int elementSuper;

    TestSuper (int elementSuper) {
        this.elementSuper = elementSuper;
    }
}

class TestSub1 extends TestSuper {
    int elementSub1;

    TestSub1 (int elementSuper, int elementSub1) {
        super (elementSuper);
        this.elementSub1 = elementSub1;
    }
}

class TestSub2 extends TestSuper {
    int elementSub2;

    TestSub2 (int elementSuper, int elementSub2) {
        super (elementSuper);
        this.elementSub2 = elementSub2;
    }
}