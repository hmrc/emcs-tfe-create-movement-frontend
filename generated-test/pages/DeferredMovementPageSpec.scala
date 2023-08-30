package pages

import pages.behaviours.PageBehaviours

class DeferredMovementPageSpec extends PageBehaviours {

  "DeferredMovementPage" - {

    beRetrievable[Boolean](DeferredMovementPage)

    beSettable[Boolean](DeferredMovementPage)

    beRemovable[Boolean](DeferredMovementPage)
  }
}
