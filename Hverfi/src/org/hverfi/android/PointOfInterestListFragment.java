package org.hverfi.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.avajadi.json.ListMapper;
import org.hverfi.android.PointOfInterestLoader.RESTResponse;
import org.hverfi.domain.PointOfInterest;
import org.hverfi.factory.PointOfInterestFactory;
import org.hverfi.factory.PointOfInterestMapper;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * A list fragment representing a list of PointsOfInterest. This fragment also
 * supports tablet devices by allowing list items to be given an 'activated'
 * state upon selection. This helps indicate which item is currently being
 * viewed in a {@link PointOfInterestDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PointOfInterestListFragment extends ListFragment implements
		LoaderCallbacks<PointOfInterestLoader.RESTResponse> {
	private List<PointOfInterest> pois = new ArrayList<PointOfInterest>();

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private static final String ARGS_URI = "URI_FIND";

	private static final String TAG = PointOfInterestListFragment.class
			.getName();

	private static final int LOADER_POI_FIND = 0x1;

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PointOfInterestListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new ArrayAdapter<PointOfInterest>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, pois));
		
		// This is our REST action.
		Uri poiFindUri = Uri.parse("http://api.hverfi.org/poi/find");
		Bundle args = new Bundle();
		args.putParcelable(ARGS_URI, poiFindUri);
		this.getActivity().getSupportLoaderManager()
				.initLoader(LOADER_POI_FIND, args, this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(((ArrayAdapter<PointOfInterest>) getListAdapter()).getItem(position).getId().toString());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override
	public Loader<RESTResponse> onCreateLoader(int id, Bundle args) {
		Log.e(TAG, String.format("onCreateLoader( %n, args )", id));
		if (args != null && args.containsKey(ARGS_URI)) {
			Uri action = args.getParcelable(ARGS_URI);
			return new PointOfInterestLoader(this.getActivity(), action);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLoadFinished(Loader<RESTResponse> loader, RESTResponse data) {
		int code = data.getCode();
		String json = data.getData();
		// Check to see if we got an HTTP 200 code and have some data.
		if (code == 200 && !json.equals("")) {
			List<PointOfInterest> pois;
			try {
				pois = new ListMapper<PointOfInterest>(
						new PointOfInterestMapper()).mapFromJSON(new JSONArray(
						json));
				
				// Load our list adapter with our POIs.
				((ArrayAdapter<PointOfInterest>) getListAdapter()).clear();
				for (PointOfInterest poi : pois) {
					((ArrayAdapter<PointOfInterest>) getListAdapter()).add(poi);
					PointOfInterestFactory.add(poi);
				}
				((ArrayAdapter<PointOfInterest>) getListAdapter())
						.notifyDataSetChanged();
			} catch (JSONException e) {
				Toast.makeText(getActivity(), e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getActivity(),
					"Failed to load POIs. Check your internet settings.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLoaderReset(Loader<RESTResponse> arg0) {
		// This method has intentionally been left blank
	}
}
