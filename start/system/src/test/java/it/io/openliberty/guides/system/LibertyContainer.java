// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.system;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class LibertyContainer extends GenericContainer<LibertyContainer> {

    public LibertyContainer(final String dockerImageName) {
        super(dockerImageName);
        // wait for smarter planet message
        waitingFor(Wait.forLogMessage("^.*CWWKF0011I.*$", 1));
        addExposedPorts(9083);
    }

}
