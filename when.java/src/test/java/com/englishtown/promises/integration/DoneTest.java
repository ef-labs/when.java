package com.englishtown.promises.integration;

import com.englishtown.promises.exceptions.RejectException;
import org.junit.Test;

/**
 * Created by adriangonzalez on 8/28/14.
 */
public class DoneTest extends AbstractIntegrationTest {

    @Test
    public void testDone_() {

        resolved(123)
                .then((x) -> {
//			throw x;
                    throw new RejectException().setValue(x);
//			return Promise.reject(x);
//			foo();
//			throw new TypeError(x);
                })
//		.then(void 0, function() { console.log(123);})
                .done(null, null);

    }

}
