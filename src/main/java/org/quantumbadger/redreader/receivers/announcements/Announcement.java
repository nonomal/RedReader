/*******************************************************************************
 * This file is part of RedReader.
 *
 * RedReader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RedReader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RedReader.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.quantumbadger.redreader.receivers.announcements;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.quantumbadger.redreader.common.UriString;
import org.quantumbadger.redreader.common.time.TimeDuration;
import org.quantumbadger.redreader.common.time.TimestampUTC;

import java.io.IOException;

public class Announcement {

	private static final String ENTRY_ID = "i";
	private static final String ENTRY_TITLE = "t";
	private static final String ENTRY_MESSAGE = "m";
	private static final String ENTRY_URL = "u";
	private static final String ENTRY_SHOW_UNTIL = "until";

	@NonNull public final String id;

	@NonNull public final String title;
	@Nullable public final String message;

	@NonNull public final UriString url;

	public final TimestampUTC showUntil;

	private Announcement(
			@NonNull final String id,
			@NonNull final String title,
			@Nullable final String message,
			@NonNull final UriString url,
			final TimestampUTC showUntil) {
		this.id = id;

		this.title = title;
		this.message = message;
		this.url = url;
		this.showUntil = showUntil;
	}

	@NonNull
	public static Announcement create(
			@NonNull final String id,
			@NonNull final String title,
			@Nullable final String message,
			@NonNull final UriString url,
			final TimeDuration duration) {

		return new Announcement(
				id,
				title,
				message,
				url,
				TimestampUTC.now().add(duration));
	}

	public boolean isExpired() {
		return showUntil.hasPassed();
	}

	@NonNull
	public Payload toPayload() {

		final Payload result = new Payload();

		result.setString(ENTRY_ID, id);
		result.setString(ENTRY_TITLE, title);

		if(message != null) {
			result.setString(ENTRY_MESSAGE, message);
		}

		result.setString(ENTRY_URL, url.value);
		result.setLong(ENTRY_SHOW_UNTIL, showUntil.toUtcMs());

		return result;
	}

	@NonNull
	public static Announcement fromPayload(@NonNull final Payload payload) throws IOException {

		String id = payload.getString(ENTRY_ID);
		final String title = payload.getString(ENTRY_TITLE);
		final String message = payload.getString(ENTRY_MESSAGE);
		final String url = payload.getString(ENTRY_URL);
		final Long showUntil = payload.getLong(ENTRY_SHOW_UNTIL);

		if(title == null || url == null || showUntil == null) {
			throw new IOException("Required entry missing");
		}

		if(id == null) {
			id = url;
		}

		return new Announcement(
				id,
				title,
				message,
				new UriString(url),
				TimestampUTC.fromUtcMs(showUntil));
	}
}
