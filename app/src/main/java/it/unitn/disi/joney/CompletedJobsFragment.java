package it.unitn.disi.joney;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompletedJobsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompletedJobsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompletedJobsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    private OnFragmentInteractionListener mListener;

    public CompletedJobsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompletedJobsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompletedJobsFragment newInstance(String param1, String param2) {
        CompletedJobsFragment fragment = new CompletedJobsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_jobs, container, false);

        final Context context = getActivity().getApplicationContext();
        DatabaseHandler db = new DatabaseHandler(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);

        final List<Job> completedJobs = db.getUserCompletedJobs(currentUserId);
        Log.i("COMPLETED_JOBS", Integer.toString(completedJobs.size()));
        for(Job job : completedJobs) {
            job.setJobCategory(db.getJobCategoryById(job.getCategoryId()));
            job.setWorker(db.getUserById(job.getWorkerId()));
            job.setAuthor(db.getUserById(job.getAuthorId()));
        }

        if(completedJobs.size() > 0) {
            expandableListView = (ExpandableListView) view.findViewById(R.id.elv_posted_jobs);
            expandableListAdapter = new ExpandableJobListAdapter(context, completedJobs, Constants.COMPLETED_JOB_TAB);
            expandableListView.setAdapter(expandableListAdapter);
            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
                    Toast.makeText(context,
                            completedJobs.get(groupPosition).getTitle() + " List Expanded.",
                            Toast.LENGTH_SHORT).show();
                }
            });

            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                @Override
                public void onGroupCollapse(int groupPosition) {
                    Toast.makeText(context,
                            completedJobs.get(groupPosition).getTitle() + " List Collapsed.",
                            Toast.LENGTH_SHORT).show();

                }
            });

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    Toast.makeText(
                            context,
                            completedJobs.get(groupPosition).getTitle()
                                    + " -> "
                                    + completedJobs.get(groupPosition).getDescription(), Toast.LENGTH_SHORT
                    ).show();
                    return false;
                }
            });
        } else {
            TextView tvInfo = (TextView) view.findViewById(R.id.tv_info);
            tvInfo.setVisibility(View.VISIBLE);
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
