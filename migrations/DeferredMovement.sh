#!/bin/bash

echo ""
echo "Applying migration DeferredMovement"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:ern/:lrn/deferredMovement                        controllers.DeferredMovementController.onPageLoad(ern: String, lrn: String, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /:ern/:lrn/deferredMovement                        controllers.DeferredMovementController.onSubmit(ern: String, lrn: String, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /:ern/:lrn/DeferredMovement/change                  controllers.DeferredMovementController.onPageLoad(ern: String, lrn: String, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /:ern/:lrn/DeferredMovement/change                  controllers.DeferredMovementController.onSubmit(ern: String, lrn: String, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "deferredMovement.title = deferredMovement" >> ../conf/messages.en
echo "deferredMovement.heading = deferredMovement" >> ../conf/messages.en
echo "deferredMovement.checkYourAnswersLabel = deferredMovement" >> ../conf/messages.en
echo "deferredMovement.error.required = Select yes if deferredMovement" >> ../conf/messages.en
echo "deferredMovement.change.hidden = DeferredMovement" >> ../conf/messages.en

echo "Adding messages to Welsh conf.messages"
echo "" >> ../conf/messages.cy
echo "deferredMovement.title = deferredMovement" >> ../conf/messages.cy
echo "deferredMovement.heading = deferredMovement" >> ../conf/messages.cy
echo "deferredMovement.checkYourAnswersLabel = deferredMovement" >> ../conf/messages.cy
echo "deferredMovement.error.required = Select yes if deferredMovement" >> ../conf/messages.cy
echo "deferredMovement.change.hidden = DeferredMovement" >> ../conf/messages.cy

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDeferredMovementUserAnswersEntry: Arbitrary[(DeferredMovementPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DeferredMovementPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDeferredMovementPage: Arbitrary[DeferredMovementPage.type] =";\
    print "    Arbitrary(DeferredMovementPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DeferredMovementPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration DeferredMovement completed"
