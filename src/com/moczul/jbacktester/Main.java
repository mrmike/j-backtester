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

import com.moczul.jbacktester.data.Portfolio;
import com.moczul.jbacktester.interfaces.MarketDataSourceable;
import com.moczul.jbacktester.interfaces.Tradable;

public class Main {

	public static void main(String[] args) {
		MarketDataSourceable feed = new PKOFeed();
		Tradable simpleStrategy = new SimpleStrategy(feed);
		Portfolio portfolio = getPortfolio();
		TestRunner pkoRunner = new TestRunner(feed, simpleStrategy, portfolio);
		pkoRunner.runTest();
	}

	private static Portfolio getPortfolio() {
		Portfolio.Builder builder = new Portfolio.Builder();
		return builder.setInitValue(10000).setMaxOrderNumber(10)
				.setGolbalStop(3000).setName("My portfolio")
				.setCommission(0.03).build();
	}
}
