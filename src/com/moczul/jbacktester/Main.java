/** 
 *
 * Copyright 2013 Michał Moczulski

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moczul.jbacktester;

import java.util.Calendar;
import java.util.Date;

public class Main {

	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2008, 0, 1);
		Date startDate = calendar.getTime();
		calendar.set(2012, 11, 31);
		Date endDate = calendar.getTime();
		
		AVBFeed avbFeed = new AVBFeed("ALTR", startDate, endDate);
		EQRFeed eqrFeed = new EQRFeed("MCHP", startDate, endDate);
		
		PairTestRunner pairTest = new PairTestRunner(avbFeed, eqrFeed);
		pairTest.startBackTest();
	}
}
