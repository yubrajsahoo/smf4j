/*
 *
 *  * Copyright 2024 Yubraj Sahoo
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.github.yubrajsahoo.smf4jcore.core.logger;


import io.github.yubrajsahoo.smf4jcore.core.domain.Metrics;

/**
 * Interface for logging metrics.
 * <p>
 * Implementations of this interface can be provided to customize the metric logging style.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public interface MetricsLogger {

    /**
     * Logs the details of a recorded {@link Metrics}.
     *
     * @param metrics the metric data to log
     */
    void log(Metrics metrics);
}
