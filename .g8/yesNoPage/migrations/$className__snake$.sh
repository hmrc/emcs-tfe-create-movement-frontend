#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trader/:ern/draft/:lrn/$className;format="decap"$                        controllers.$className$Controller.onPageLoad(ern: String, lrn: String, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trader/:ern/draft/:lrn/$className;format="decap"$                        controllers.$className$Controller.onSubmit(ern: String, lrn: String, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /trader/:ern/draft/:lrn/$className$/change                  controllers.$className$Controller.onPageLoad(ern: String, lrn: String, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /trader/:ern/draft/:lrn/$className$/change                  controllers.$className$Controller.onSubmit(ern: String, lrn: String, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.error.required = Select yes if $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages.en

echo "Adding messages to Welsh conf.messages"
echo "" >> ../conf/messages.cy
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.cy
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.cy
echo "$className;format="decap"$.checkYourAnswersLabel = $className;format="decap"$" >> ../conf/messages.cy
echo "$className;format="decap"$.error.required = Select yes if $className;format="decap"$" >> ../conf/messages.cy
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages.cy

echo "Migration $className;format="snake"$ completed"
