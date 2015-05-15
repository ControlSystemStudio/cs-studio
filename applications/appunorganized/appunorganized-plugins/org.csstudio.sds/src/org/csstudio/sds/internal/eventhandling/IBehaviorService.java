package org.csstudio.sds.internal.eventhandling;

import java.util.List;

import org.csstudio.sds.eventhandling.AbstractBehavior;

public interface IBehaviorService {

    AbstractBehavior<?> getBehavior(String behaviorId, String widgetId);

    List<BehaviorDescriptor> getBehaviors(String widgetId);

    String[] getInvisiblePropertyIds(String behaviorId, String widgetId);

}