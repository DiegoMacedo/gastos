package com.dmo.gastos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmo.gastos.R;
import com.dmo.gastos.model.Gasto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GastoAdapter extends RecyclerView.Adapter<GastoAdapter.GastoHolder> {

    private List<Gasto> gastos = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditarClick(Gasto gasto);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GastoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasto, parent, false);
        return new GastoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GastoHolder holder, int position) {
        Gasto gastoAtual = gastos.get(position);
        holder.textViewDescricao.setText(gastoAtual.getDescricao());
        holder.textViewCategoria.setText(gastoAtual.getCategoria());

        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(Locale.getDefault());
        holder.textViewValor.setText(formatoMoeda.format(gastoAtual.getValor()));
    }

    @Override
    public int getItemCount() {
        return gastos.size();
    }

    public void setGastos(List<Gasto> gastos) {
        this.gastos = gastos;
        notifyDataSetChanged();
    }

    public Gasto getGastoAt(int position) {
        return gastos.get(position);
    }

    class GastoHolder extends RecyclerView.ViewHolder {
        private TextView textViewDescricao;
        private TextView textViewValor;
        private TextView textViewCategoria;
        private ImageView btnEditar; // NOVO

        public GastoHolder(@NonNull View itemView) {
            super(itemView);
            textViewDescricao = itemView.findViewById(R.id.tvItemDescricao);
            textViewValor = itemView.findViewById(R.id.tvItemValor);
            textViewCategoria = itemView.findViewById(R.id.tvItemCategoria);
            btnEditar = itemView.findViewById(R.id.btnEditar);

            btnEditar.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onEditarClick(gastos.get(position));
                }
            });
        }
    }
}
