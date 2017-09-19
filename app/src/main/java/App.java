/**
 * Created by michael on 19/09/17.
 */

class App {
    private static final App ourInstance = new App();

    static App getInstance() {
        return ourInstance;
    }

    private App() {
    }
}
