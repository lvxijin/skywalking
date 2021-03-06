/*
 * Copyright 2017, OpenSkywalking Organization All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Project repository: https://github.com/OpenSkywalking/skywalking
 */

package org.skywalking.apm.plugin.jedis.v2.define;

import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.skywalking.apm.plugin.jedis.v2.JedisClusterConstructorWithHostAndPortArgInterceptor;
import org.skywalking.apm.plugin.jedis.v2.JedisClusterConstructorWithListHostAndPortArgInterceptor;
import org.skywalking.apm.plugin.jedis.v2.JedisMethodInterceptor;
import org.skywalking.apm.plugin.jedis.v2.RedisMethodMatch;

import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static org.skywalking.apm.agent.core.plugin.bytebuddy.ArgumentTypeNameMatch.takesArgumentWithType;
import static org.skywalking.apm.agent.core.plugin.match.NameMatch.byName;

/**
 * {@link JedisClusterInstrumentation} presents that skywalking intercepts all constructors and methods of {@link
 * redis.clients.jedis.JedisCluster}. {@link JedisClusterConstructorWithHostAndPortArgInterceptor}
 * intercepts all constructor with argument {@link redis.clients.jedis.HostAndPort} and the other constructor intercept
 * by class {@link JedisClusterConstructorWithListHostAndPortArgInterceptor}. {@link JedisMethodInterceptor} intercept
 * all methods of {@link redis.clients.jedis.JedisCluster}
 *
 * @author zhangxin
 */
public class JedisClusterInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ARGUMENT_TYPE_NAME = "redis.clients.jedis.HostAndPort";
    private static final String ENHANCE_CLASS = "redis.clients.jedis.JedisCluster";
    private static final String CONSTRUCTOR_WITH_LIST_HOSTANDPORT_ARG_INTERCEPT_CLASS = "org.skywalking.apm.plugin.jedis.v2.JedisClusterConstructorWithListHostAndPortArgInterceptor";
    private static final String METHOD_INTERCEPT_CLASS = "org.skywalking.apm.plugin.jedis.v2.JedisMethodInterceptor";
    private static final String CONSTRUCTOR_WITH_HOSTANDPORT_ARG_INTERCEPT_CLASS = "org.skywalking.apm.plugin.jedis.v2.JedisClusterConstructorWithHostAndPortArgInterceptor";

    @Override
    public ClassMatch enhanceClass() {
        return byName(ENHANCE_CLASS);
    }

    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[] {
            new ConstructorInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getConstructorMatcher() {
                    return takesArgument(0, Set.class);
                }

                @Override
                public String getConstructorInterceptor() {
                    return CONSTRUCTOR_WITH_LIST_HOSTANDPORT_ARG_INTERCEPT_CLASS;
                }
            },
            new ConstructorInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getConstructorMatcher() {
                    return takesArgumentWithType(0, ARGUMENT_TYPE_NAME);
                }

                @Override
                public String getConstructorInterceptor() {
                    return CONSTRUCTOR_WITH_HOSTANDPORT_ARG_INTERCEPT_CLASS;
                }
            }
        };
    }

    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return RedisMethodMatch.INSTANCE.getJedisClusterMethodMatcher();
                }

                @Override
                public String getMethodsInterceptor() {
                    return METHOD_INTERCEPT_CLASS;
                }

                @Override public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }
}
