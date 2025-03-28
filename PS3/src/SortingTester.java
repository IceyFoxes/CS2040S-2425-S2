import java.util.Random;

public class SortingTester {
    public static boolean checkSort(ISort sorter, int size) {
        // TODO: implement this
        KeyValuePair[] randomArray = new KeyValuePair[size];
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            int j = rand.nextInt(-1000000, 1000000);
            int k = rand.nextInt(-1000000, 1000000);
            randomArray[i] = new KeyValuePair(j, k);
        }

        //Test BubbleSort vs InsertionSort (nearly sorted array)
        KeyValuePair[] nearlySortedArray = new KeyValuePair[size];
        for (int i = 0; i < size; i ++) {
            nearlySortedArray[i] = new KeyValuePair(i, i);
        }
        nearlySortedArray[(int) (size / 2)] = new KeyValuePair(-1, -1);

        // Do the sorting
        long sortCost = sorter.sort(randomArray);
        System.out.println(sortCost);

        for (int i = 0; i < size - 1; i++) {
            if (randomArray[i].compareTo(randomArray[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isStable(ISort sorter, int size) {
        // TODO: implement this
        if (size == 0 || size == 1) {
            return true;
        }
        int newSize = 100;

        KeyValuePair[] randomArray = new KeyValuePair[newSize];
        Random rand = new Random();

        for (int i = 0; i < newSize; i++) {
            int j = rand.nextInt(1, newSize - 1);
            randomArray[i] = new KeyValuePair( j, i );
        }

        // Do the sorting
        sorter.sort(randomArray);

        for (int i = 0; i < newSize - 1; i++) {
            if (randomArray[i].compareTo(randomArray[i + 1]) == 0 &&
                    randomArray[i].getValue() > randomArray[i + 1].getValue()) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        // TODO: implement this
//        ISort[] sorters = {
//                new SorterA(),
//                new SorterB(),
//                new SorterC(),
//                new SorterD(),
//                new SorterE(),
//                new SorterF()
//        };
        //Catch Dr.Evil
//        Caught:
//        for (int i = 0; i < 10; i++) {
//            for (ISort sorter : sorters) {
//                if (!checkSort(sorter, 10000)) {
//                    System.out.println("Dr Evil: " + sorter); //SorterB
//                    break Caught;
//                }
//            }
//        }

//        for (ISort sorter : sorters) {
//            System.out.println(sorter + ":");
//            System.out.println("Check Sort: " + checkSort(sorter, 100));
//            System.out.println("Is Stable: " + isStable(sorter, 3));
//            System.out.println("-----------------------------------------------");
//        }
    }
}
