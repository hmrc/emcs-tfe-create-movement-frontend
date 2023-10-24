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
echo "$className;format="decap"$.title = $title$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $title$" >> ../conf/messages.en
echo "$className;format="decap"$.$option1key;format="decap"$ = $option1msg$" >> ../conf/messages.en
echo "$className;format="decap"$.$option2key;format="decap"$ = $option2msg$" >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $title$" >> ../conf/messages.en
echo "$className;format="decap"$.error.required = Select $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages.en

echo "Adding messages to Welsh conf.messages"
echo "" >> ../conf/messages.cy
echo "$className;format="decap"$.title = $title$" >> ../conf/messages.cy
echo "$className;format="decap"$.heading = $title$" >> ../conf/messages.cy
echo "$className;format="decap"$.$option1key;format="decap"$ = $option1msg$" >> ../conf/messages.cy
echo "$className;format="decap"$.$option2key;format="decap"$ = $option2msg$" >> ../conf/messages.cy
echo "$className;format="decap"$.checkYourAnswersLabel = $title$" >> ../conf/messages.cy
echo "$className;format="decap"$.error.required = Select $className;format="decap"$" >> ../conf/messages.cy
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages.cy

echo "Migration $className;format="snake"$ completed"
