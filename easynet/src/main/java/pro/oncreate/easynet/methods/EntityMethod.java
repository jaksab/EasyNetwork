package pro.oncreate.easynet.methods;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

@SuppressWarnings("unused,WeakerAccess")
public abstract class EntityMethod extends Method {

    @Override
    public boolean withBody() {
        return true;
    }
}
