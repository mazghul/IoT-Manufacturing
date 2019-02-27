package wilp.bits.iotmanufacturing.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import wilp.bits.iotmanufacturing.Model.EventResponse;
import wilp.bits.iotmanufacturing.Model.Manu;
import wilp.bits.iotmanufacturing.Provider.VolleyReq;
import wilp.bits.iotmanufacturing.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    RequestQueue mRequestQueue;
    LineGraphSeries<DataPoint> series;
    SimpleDateFormat dateFormat;


    public GraphFragment() {
        // Required empty public constructor
    }

    public static GraphFragment newInstance() {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_graph, container, false);
        final GraphView graph = root.findViewById(R.id.graph);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(dateFormat.parse("2019-02-19T12:00:00"), 25),
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
        graph.addSeries(series);

        VolleyReq.get_events(eventResponseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }).enqueue(mRequestQueue);
        return root;
    }

    private Response.Listener<EventResponse> eventResponseListener = new Response.Listener<EventResponse>() {
        @Override
        public void onResponse(EventResponse response) {
            for (Manu var : response.getResponse())
                try {
                    series.appendData(new DataPoint(dateFormat.parse(var.getTime()), Integer.valueOf((var.getTemp()))), true, 50);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
        }
    };


}
