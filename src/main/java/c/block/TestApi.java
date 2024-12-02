package c.block;

import c.block.api.IitisLabsAPIService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class TestApi {

    public static void main(String[] args) {
        try {
            System.out.println(IitisLabsAPIService.ts("30820277020100300d06092a864886f70d010101050004820261302025"));
            System.out.println(IitisLabsAPIService.publicKey());
            System.out.println(IitisLabsAPIService.publicKey64());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
