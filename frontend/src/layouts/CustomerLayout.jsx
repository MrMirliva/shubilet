import { Outlet } from "react-router-dom";
import CustomerTopBar from "../components/Customer/CustomerTopBar/CustomerTopBar.jsx";
import "./CustomerLayout.css";

export default function CustomerLayout() {
  return (
    <>
      <CustomerTopBar />
      <Outlet />
    </>
  );
}