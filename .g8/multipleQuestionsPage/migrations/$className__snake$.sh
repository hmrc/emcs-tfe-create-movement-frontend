#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trader/:ern/draft/:draftId/$className;format="decap"$                        controllers.$className$Controller.onPageLoad(ern: String, lrn: String, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trader/:ern/draft/:draftId/$className;format="decap"$                        controllers.$className$Controller.onSubmit(ern: String, lrn: String, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /trader/:ern/draft/:draftId/$className$/change                  controllers.$className$Controller.onPageLoad(ern: String, lrn: String, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /trader/:ern/draft/:draftId/$className$/change                  controllers.$className$Controller.onSubmit(ern: String, lrn: String, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.$field1Name$ = $field1Name$" >> ../conf/messages.en
echo "$className;format="decap"$.$field2Name$ = $field2Name$" >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $className$" >> ../conf/messages.en
echo "$className;format="decap"$.error.$field1Name$.required = Enter $field1Name$" >> ../conf/messages.en
echo "$className;format="decap"$.error.$field2Name$.required = Enter $field2Name$" >> ../conf/messages.en
echo "$className;format="decap"$.error.$field1Name$.length = $field1Name$ must be $field1MaxLength$ characters or less" >> ../conf/messages.en
echo "$className;format="decap"$.error.$field2Name$.length = $field2Name$ must be $field2MaxLength$ characters or less" >> ../conf/messages.en
echo "$className;format="decap"$.$field1Name$.change.hidden = $field1Name$" >> ../conf/messages.en
echo "$className;format="decap"$.$field2Name$.change.hidden = $field2Name$" >> ../conf/messages.en

echo "Adding messages to Welsh conf.messages"
echo "" >> ../conf/messages.cy
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.cy
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.cy
echo "$className;format="decap"$.$field1Name$ = $field1Name$" >> ../conf/messages.cy
echo "$className;format="decap"$.$field2Name$ = $field2Name$" >> ../conf/messages.cy
echo "$className;format="decap"$.checkYourAnswersLabel = $className$" >> ../conf/messages.cy
echo "$className;format="decap"$.error.$field1Name$.required = Enter $field1Name$" >> ../conf/messages.cy
echo "$className;format="decap"$.error.$field2Name$.required = Enter $field2Name$" >> ../conf/messages.cy
echo "$className;format="decap"$.error.$field1Name$.length = $field1Name$ must be $field1MaxLength$ characters or less" >> ../conf/messages.cy
echo "$className;format="decap"$.error.$field2Name$.length = $field2Name$ must be $field2MaxLength$ characters or less" >> ../conf/messages.cy
echo "$className;format="decap"$.$field1Name$.change.hidden = $field1Name$" >> ../conf/messages.cy
echo "$className;format="decap"$.$field2Name$.change.hidden = $field2Name$" >> ../conf/messages.cy

echo "Migration $className;format="snake"$ completed"
