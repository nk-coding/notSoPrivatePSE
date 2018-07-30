import java.io.IOException;

import de.unistuttgart.iste.rss.oo.hamstersimulator.HamsterSimulator;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.Hamster;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.HamsterStateListener;
import de.unistuttgart.iste.rss.oo.hamstersimulator.territory.Territory;
import de.unistuttgart.iste.rss.oo.hamstersimulator.ui.javafx.JavaFXUI;

public class Main {

    public static HamsterStateListener l;

    public static void main(final String[] args) throws IOException {
        final String territoryFile = "/Users/snowball/test.ter";
        final HamsterSimulator simulator = new HamsterSimulator();

        JavaFXUI.start();
        JavaFXUI.getSingleton().init(simulator.getTerritory());


        //        class MyFlowableOnSubscribe implements FlowableOnSubscribe<HamsterStateChangedEvent> {
        //            @Override
        //            public void subscribe(final FlowableEmitter<HamsterStateChangedEvent> source) throws Exception {
        //                final HamsterStateListener listener = event -> source.onNext(event);
        //                paule.addHamsterStateListener(listener);
        //                l = listener;
        //            }
        //        }
        //        final MyFlowableOnSubscribe fos = new MyFlowableOnSubscribe();
        //
        //        final Flowable<HamsterStateChangedEvent> hamsterStateObservable =
        //                Flowable.create(fos, BackpressureStrategy.BUFFER).
        //                doOnCancel(()->System.out.println("Canceled")).
        //                doOnComplete(()->System.out.println("Completed")).
        //                doFinally(()->paule.removeHamsterStateListener(l));
        //
        //        final Disposable stateObserverDisposable = hamsterStateObservable.subscribe(System.out::println);

        final Territory territory = simulator.getTerritory();
        // territory.setSize(6, 3).defaultHamsterAt(1, 1, Direction.EAST, 0).wallAt(0, 0).wallAt(1, 5).wallAt(1,0).grainAt(1, 4, 2);
        territory.loadTerritoryFromFile(territoryFile);
        try {
            Thread.sleep(2000);
        } catch (final InterruptedException e) { }


        exampleRun(simulator);
        //        stateObserverDisposable.dispose();
    }

    private static void exampleRun(final HamsterSimulator simulator) {
        final Hamster paule = simulator.getTerritory().getDefaultHamster();
        paule.addHamsterStateListener(e -> System.out.println(e));
        while (!paule.grainAvailable() && paule.frontIsClear()) {
            paule.move();
        }
        while (paule.grainAvailable()) {
            paule.pickGrain();
        }
        paule.turnLeft();
        paule.turnLeft();
        while (paule.frontIsClear()) {
            paule.move();
        }
        while (!paule.mouthEmpty()) {
            paule.putGrain();
        }
    }
}
