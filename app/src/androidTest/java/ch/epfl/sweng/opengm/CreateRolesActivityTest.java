package ch.epfl.sweng.opengm;

import android.test.ActivityInstrumentationTestCase2;
import android.support.test.InstrumentationRegistry;

public class CreateRolesActivityTest extends ActivityInstrumentationTestCase2<CreateRoles>{

    public CreateRolesActivityTest() {
        super(CreateRoles.class);
    }

    @Override
    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }


}
